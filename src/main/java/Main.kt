import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
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
        val uuid = row["game_uuid"] as String
        val redPlayer = Player(row["red_username"] as String, row["red_is_guest"] as Boolean, row["red_is_banned"] as Boolean, row["red_rating"] as Int)
        val blackPlayer = Player(row["black_username"] as String, row["black_is_guest"] as Boolean, row["black_is_banned"] as Boolean, row["black_rating"] as Int)
        val movesWithThinkTime = GameImportingService().convertToListOfMoves(row["moves_raw"] as String)
        val moves = movesWithThinkTime.map { it.first }
        val gameTimer = row["game_timer"] as Int
        val moveTimer = row["move_timer"] as Int
        val increment = row["increment"] as Int
        val endReason = GameResultReason.valueOf((row["end_reason"] as String).uppercase().replace(" ", "_"))
        val resultRed = GameResult.valueOf((row["result_red"] as String).uppercase())
        val resultBlack = GameResult.valueOf((row["result_black"] as String).uppercase())
        val game = Game(uuid, redPlayer, blackPlayer, gameTimer, moveTimer, increment, moves, resultBlack, resultRed, endReason)
        games.add(game)
        if(games.size > 100) break
    }
    println("Finished importing games!")
    val pikafishInstances = ArrayList<Pikafish>()
    (0..<ConfigOptions.pikafishPoolSize).forEach { _ -> pikafishInstances.add(Pikafish(ConfigOptions)) }
    val pool = Channel<Pikafish>(pikafishInstances.size)
    pikafishInstances.forEach { pool.trySend(it) }
    println("Finished building Pikafish instance!")

    suspend fun processGames(games: List<Game>) = coroutineScope {
        games.map { game ->
            async(Dispatchers.Default) {
                val pikafish = pool.receive()
                try {
                    val evaluationsRed = game.moves.indices.map { i ->
                        val board = pikafish.makeMoves(Board.STARTING_BOARD, game.moves.take(i + 1))
                        val eval = pikafish.evaluate(board)
                        if (i % 2 == 0) eval.flip() else eval
                    }
                    println(evaluationsRed)
                }
                finally {
                    pikafish.clear()
                    pool.send(pikafish)
                }
            }
        }
        }.awaitAll()
    val allEvaluations = runBlocking { processGames(games) }
    println("Evaluated all games!")
}