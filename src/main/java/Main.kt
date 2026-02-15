import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv

private data class GameWithThinkTimes(val game : Game, val redThinkTimes : List<Int>, val blackThinkTimes : List<Int>)

fun main(){
    //TODO: move these to command line args
    val pathToExecutable = "/Volumes/COLDSTORAGE/Kiwi Computing/Xiangqi/Pikafish/src/pikafish"
    val pikafishPoolSize = 1
    val numThreads = 8
    val hashSizeMiB = 128
    val nodesToSearchPerMove = 250_000
    val csvFilepath = "/Volumes/COLDSTORAGE/Kiwi Computing/Xiangqi/chunks/chunk_ki.csv"

    val df = DataFrame.readCsv(csvFilepath)
    val games = ArrayList<GameWithThinkTimes>()
    for(row in df){
        val uuid = row["game_uuid"] as String
        val redPlayer = Player(row["red_username"] as String, row["red_is_guest"] as Boolean, row["red_is_banned"] as Boolean, row["red_rating"] as Int)
        val blackPlayer = Player(row["black_username"] as String, row["black_is_guest"] as Boolean, row["black_is_banned"] as Boolean, row["black_rating"] as Int)
        val movesWithThinkTime = GameImportingService().convertToListOfMoves(row["moves_raw"] as String)
        val moves = movesWithThinkTime.map { it.first }
        val thinkTimes = movesWithThinkTime.map { it.second }
        val redThinkTimes = thinkTimes.filterIndexed { index, _ ->  index.mod(2) == 0}
        val blackThinkTimes = thinkTimes.filterIndexed { index, _ ->  index.mod(2) == 1}
        val gameTimer = row["game_timer"] as Int
        val moveTimer = row["move_timer"] as Int
        val increment = row["increment"] as Int
        val endReason = GameResultReason.valueOf((row["end_reason"] as String).uppercase().replace(" ", "_"))
        val resultRed = GameResult.valueOf((row["result_red"] as String).uppercase())
        val resultBlack = GameResult.valueOf((row["result_black"] as String).uppercase())
        val game = Game(uuid, redPlayer, blackPlayer, gameTimer, moveTimer, increment, moves, resultBlack, resultRed, endReason)

        games.add(GameWithThinkTimes(game, redThinkTimes, blackThinkTimes))
        if(games.size >= 10) break
    }
    val pikafishInstances = ArrayList<Pikafish>()
    (1..pikafishPoolSize).forEach { _ -> pikafishInstances.add(Pikafish(pathToExecutable, numThreads, hashSizeMiB)) }
    val pool = Channel<Pikafish>(pikafishInstances.size)
    pikafishInstances.forEach { pool.trySend(it) }

    suspend fun reviewGames(games: List<Game>) = coroutineScope {
        games.map { game ->
            async(Dispatchers.Default) {
                val pikafish = pool.receive()
                val gameReviewService = GameReviewService(pikafish)
                val reviewedGame = gameReviewService.review(game, nodesToSearchPerMove)
                pikafish.clear()
                pool.send(pikafish)
                println("$reviewedGame")
                return@async reviewedGame
            }
        }
    }.awaitAll()

    val allReviewedGames = runBlocking { reviewGames(games.map(GameWithThinkTimes::game)) }
    val features = games.zip(allReviewedGames).map { (gameWithThinkTime, reviewedGame) ->
        val featureExtractionService = FeatureExtractionService()
        val features = featureExtractionService.getFeatures(reviewedGame, gameWithThinkTime.redThinkTimes, gameWithThinkTime.blackThinkTimes)
        return@map features.toNumerical()
    }

    println(features)
}