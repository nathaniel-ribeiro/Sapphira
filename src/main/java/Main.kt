import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv

fun main(args : Array<String>){
    if(args.size != 1) throw IllegalArgumentException()
    // create new dataframe for anomaly detection:
    // game_format_bullet?
    // game_format_blitz?
    // game_format_rapid?
    // game_format_slow?
    // game_format_untimed?
    // game_type,
    // end_reason,
    // username_similarity,
    // red_rating,
    // black_rating,
    // result_red_checkmate?,
    // result_red_resign?,
    // result_red_disconnect?,
    // result_red_draw?
    // result_red_timer_expired?
    // result_black_checkmate?,
    // result_black_resign?
    // result_black_disconnect?
    // result_black_draw?
    // result_black_timer_expired?
    // move_count,
    // red_think_time_mean
    // red_think_time_std
    // black_think_time_std
    // black_think_time_std
    // avg_cp_loss_red (excluding first 14 plies, endgame positions, and positions where one side has overwhelming advantage),
    // avg_cp_loss_black (excluding first 14 plies, endgame positions, and positions where one side has overwhelming advantage),
    val df = DataFrame.readCsv(args[0])
    val games = ArrayList<Game>()
    for(row in df){
        val redPlayer = Player(row["red_username"] as String, row["red_is_guest"] as Boolean, row["red_is_banned"] as Boolean, row["red_rating"] as Int)
        val blackPlayer = Player(row["black_username"] as String, row["black_is_guest"] as Boolean, row["black_is_banned"] as Boolean, row["black_rating"] as Int)
        val usernameSimilarity = JaroWinklerSimilarity().apply(redPlayer.username, blackPlayer.username)
        val movesWithThinkTime = GameImportingService().convertToListOfMoves(row["moves_raw"] as String)
        val moves = movesWithThinkTime.map { it.first }
        val thinkTimes = movesWithThinkTime.map { it.second }
        val redThinkTimes = thinkTimes.filterIndexed { index, _ ->  index.mod(2) == 0}
        val blackThinkTimes = thinkTimes.filterIndexed { index, _ ->  index.mod(2) == 1}

        val redThinkTimeMean = redThinkTimes.average()
        val redThinkTimeStd = StandardDeviation().evaluate(redThinkTimes.toIntArray().map { it.toDouble() }.toDoubleArray())

        val blackThinkTimeMean = blackThinkTimes.average()
        val blackThinkTimeStd = StandardDeviation().evaluate(blackThinkTimes.toIntArray().map { it.toDouble() }.toDoubleArray())

        val gameTimer = row["game_timer"] as Int
        val moveTimer = row["move_timer"] as Int
        val increment = row["increment"] as Int
        val endReason = GameResultReason.valueOf((row["end_reason"] as String).uppercase().replace(" ", "_"))
        val resultRed = GameResult.valueOf((row["result_red"] as String).uppercase())
        val resultBlack = GameResult.valueOf((row["result_black"] as String).uppercase())
        val game = Game(redPlayer, blackPlayer, gameTimer, moveTimer, increment, moves, resultBlack, resultRed, endReason)
        games.add(game)
    }
    println("Finished importing games!")
    val pikafish = Pikafish(ConfigOptions)
    println("Finished building Pikafish instance!")
    val game = games[42]
    println(game.moves)
    val evaluationsRedPerspective = game.moves.indices.map { pikafish.makeMoves(Board.STARTING_BOARD, game.moves.take(it + 1)) }.mapIndexed { index, board -> if(index.mod(2) == 0) pikafish.evaluate(board).flip() else pikafish.evaluate(board) }
    println(evaluationsRedPerspective)
    println("Finished game review!")
    val featureExtractionService = FeatureExtractionService(ConfigOptions)
    val cpLossesRed = featureExtractionService.getAdjustedCPLosses(evaluationsRedPerspective, Alliance.BLACK)
    println(cpLossesRed)
    println(featureExtractionService.getMoveQualityFrequencies(cpLossesRed))
}