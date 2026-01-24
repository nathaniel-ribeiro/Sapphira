import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
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
        if(games.size >= 2) break
    }
    val pikafishInstances = ArrayList<Pikafish>()
    (0..<ConfigOptions.pikafishPoolSize).forEach { _ -> pikafishInstances.add(Pikafish(ConfigOptions)) }
    val pool = Channel<Pikafish>(pikafishInstances.size)
    pikafishInstances.forEach { pool.trySend(it) }

    suspend fun reviewGames(games: List<Game>) = coroutineScope {
        games.map { game ->
            async(Dispatchers.Default) {
                val pikafish = pool.receive()
                try {
                    val gameReviewService = GameReviewService(pikafish)
                    gameReviewService.review(game)
                }
                finally {
                    pikafish.clear()
                    pool.send(pikafish)
                }
            }
        }
    }.awaitAll()
    val allReviewedGames = runBlocking { reviewGames(games) }
    val featureExtractionService = FeatureExtractionService(ConfigOptions)
    val reviewedGame = allReviewedGames[1]
    println("First game: ${reviewedGame.game.uuid}")
    println("Game length plies: ${featureExtractionService.getTotalPlies(reviewedGame)}")
    println("Blunder rate red: ${featureExtractionService.getBlunderRate(reviewedGame, Alliance.RED)}")
    println("Blunder rate black: ${featureExtractionService.getBlunderRate(reviewedGame, Alliance.BLACK)}")
    try{
        val adjustedCPLossRed = featureExtractionService.getAdjustedCPLoss(reviewedGame, Alliance.RED)
        println("Adjusted CP loss red: $adjustedCPLossRed")
    }
    catch(exception : Exception){
        println("Adjusted CP loss red: ???")
    }

    try{
        val adjustedCPLossBlack = featureExtractionService.getAdjustedCPLoss(reviewedGame, Alliance.BLACK)
        println("Adjusted CP loss black: $adjustedCPLossBlack")
    }
    catch(exception : Exception){
        println("Adjusted CP loss black: ???")
    }
    println("Longest best/excellent streak red: ${featureExtractionService.getLongestBestOrExcellentStreak(reviewedGame, Alliance.RED)}")
    println("Longest best/excellent streak black: ${featureExtractionService.getLongestBestOrExcellentStreak(reviewedGame, Alliance.BLACK)}")
    println("Blunder inter-arrival time red: ${featureExtractionService.getAverageBlunderInterarrivalTime(reviewedGame, Alliance.RED)}")
    println("Blunder inter-arrival time black: ${featureExtractionService.getAverageBlunderInterarrivalTime(reviewedGame, Alliance.BLACK)}")
    println("Accuracy red: ${featureExtractionService.getAccuracy(reviewedGame, Alliance.RED)}")
    println("Accuracy black: ${featureExtractionService.getAccuracy(reviewedGame, Alliance.BLACK)}")
    println("JaroWinkler similarity: ${featureExtractionService.getUsernameSimilarity(reviewedGame)}")
    println("Evaluated all games!")
}