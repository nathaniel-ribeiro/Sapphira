import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv

fun main(){
    //TODO: move these to command line args
    val pathToExecutable = "/u/cwa6zf/Pikafish/src/pikafish"
    val pikafishPoolSize = 1
    val numThreads = 8
    val hashSizeMiB = 128
    val nodesToSearchPerMove = 3_500_000
    val csvFilepath = "/u/cwa6zf/chunk_ki.csv"

    val df = DataFrame.readCsv(csvFilepath)
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

    val allReviewedGames = runBlocking { reviewGames(games) }
    val featureExtractionService = FeatureExtractionService()
    // val allFeatures = buildList { addAll(allReviewedGames.map { featureExtractionService.getFeatures(it) }) }
}