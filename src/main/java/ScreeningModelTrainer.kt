import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import smile.feature.imputation.KNNImputer
import smile.feature.imputation.SVDImputer
import smile.feature.imputation.SimpleImputer
import java.io.File
import smile.anomaly.IsolationForest

class ScreeningModelTrainer : CliktCommand() {
    val csvFilePath : File by argument("--data", help="Path to the training data (in CSV format).").file()
    val pikafishExecutable : File by argument("--exe", help="Path to the Pikafish executable.").file()
    val pikafishPoolSize : Int by option("--pool", help="Number of concurrent Pikafish instances used for analysis.").int().restrictTo(1..Int.MAX_VALUE).default(1)
    val numThreads : Int by option("--threads", help="Number of threads used for *each* Pikafish instance.").int().default(Pikafish.DEFAULT_THREADS)
    val hashSizeMiB : Int by option("--hash", help="Hash size for *each* Pikafish instance in MiB.").int().default(Pikafish.DEFAULT_HASH_SIZE_MIB)
    val nodesToSearchPerMove : Int by option("--nodes", help="Minimum number of nodes to search per move in each game.").int().restrictTo(1..Int.MAX_VALUE).default(GameReviewService.DEFAULT_NODES_TO_SEARCH_PER_MOVE)

    override fun run() {
        val df = DataFrame.readCsv(csvFilePath)
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
            // TODO: remove this, just for debugging
            if(games.size > 100) break
        }
        val pikafishInstances = ArrayList<Pikafish>()
        (1..pikafishPoolSize).forEach { _ -> pikafishInstances.add(Pikafish(pikafishExecutable, numThreads, hashSizeMiB)) }
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
                    return@async reviewedGame
                }
            }
        }.awaitAll()

        val allReviewedGames = runBlocking { reviewGames(games) }
        val featureExtractionService = FeatureExtractionService()
        val allFeatures = buildList { addAll(allReviewedGames.map { featureExtractionService.getFeatures(it) }) }
        val encoder = Encoder()
        val data = allFeatures.map { encoder.encode(it) }.toTypedArray()
        val dataImputed = SVDImputer.impute(data, 5, 10)
        val isolationForest = IsolationForest.fit(dataImputed)
        println(isolationForest)
    }
}

fun main(args : Array<String>) = ScreeningModelTrainer().main(args)