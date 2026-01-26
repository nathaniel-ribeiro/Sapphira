import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import smile.anomaly.IsolationForest
import smile.feature.imputation.KNNImputer
import smile.feature.imputation.SVDImputer
import smile.data.DataFrame as SMILEDataFrame

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
        if(games.size >= 100) break
    }
    val pikafishInstances = ArrayList<Pikafish>()
    (1..ConfigOptions.pikafishPoolSize).forEach { _ -> pikafishInstances.add(Pikafish(ConfigOptions)) }
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

    fun getFeatures(reviewedGame: ReviewedGame) : List<Double>{
        val extractor = FeatureExtractionService(ConfigOptions)
        val gameTimer = reviewedGame.game.gameTimer
        val moveTimer = reviewedGame.game.moveTimer
        val increment = reviewedGame.game.increment
        //TODO: categorical features + think time statistics
        val usernameSimilarity = extractor.getUsernameSimilarity(reviewedGame)
        val redRating = reviewedGame.game.redPlayer.rating
        val blackRating = reviewedGame.game.blackPlayer.rating
        val totalPlies = extractor.getTotalPlies(reviewedGame)
        val blunderRateRed = extractor.getBlunderRate(reviewedGame, Alliance.RED)
        val blunderRateBlack = extractor.getBlunderRate(reviewedGame, Alliance.BLACK)
        val blunderInterarrivalRed = extractor.getAverageBlunderInterarrivalTime(reviewedGame, Alliance.RED)
        val blunderInterarrivalBlack = extractor.getAverageBlunderInterarrivalTime(reviewedGame, Alliance.BLACK)
        val adjustedCPLossRed = extractor.getAdjustedCPLoss(reviewedGame, Alliance.RED)
        val adjustedCPLossBlack = extractor.getAdjustedCPLoss(reviewedGame, Alliance.BLACK)
        val longestBestOrExcellentStreakRed = extractor.getLongestBestOrExcellentStreak(reviewedGame, Alliance.RED)
        val longestBestOrExcellentStreakBlack = extractor.getLongestBestOrExcellentStreak(reviewedGame, Alliance.BLACK)
        val accuracyRed = extractor.getAccuracy(reviewedGame, Alliance.RED)
        val accuracyBlack = extractor.getAccuracy(reviewedGame, Alliance.BLACK)

        return listOf(gameTimer.toDouble(), moveTimer.toDouble(), increment.toDouble(), usernameSimilarity,
                      redRating.toDouble(), blackRating.toDouble(), totalPlies.toDouble(), blunderRateRed,
                      blunderRateBlack, blunderInterarrivalRed, blunderInterarrivalBlack, adjustedCPLossRed ?: Double.NaN,
                      adjustedCPLossBlack ?: Double.NaN, longestBestOrExcellentStreakRed.toDouble(), longestBestOrExcellentStreakBlack.toDouble(),
                      accuracyRed, accuracyBlack)
    }
    val allReviewedGames = runBlocking { reviewGames(games) }
    val allFeatures = buildList { addAll(allReviewedGames.map { getFeatures(it) }) }
    val data = allFeatures.map { it.toDoubleArray() }.toTypedArray()
    println(data.contentDeepToString())
    val imputedData = SVDImputer.impute(data, 5, 10)
    println(imputedData.contentDeepToString())
    val iForest = IsolationForest.fit(imputedData)
}