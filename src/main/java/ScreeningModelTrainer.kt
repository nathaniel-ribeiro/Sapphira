import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File

class ScreeningModelTrainer : CliktCommand() {
    val csvFilePath : File by argument("--data", help="Path to the training data (in CSV format).").file()
    val pikafishExecutable : File by argument("--exe", help="Path to the Pikafish executable.").file()
    val pikafishPoolSize : Int by option("--pool", help="Number of concurrent Pikafish instances used for analysis.").int().restrictTo(1..Int.MAX_VALUE).default(1)
    val numThreads : Int by option("--threads", help="Number of threads used for *each* Pikafish instance.").int().default(Pikafish.DEFAULT_THREADS)
    val hashSizeMiB : Int by option("--hash", help="Hash size for *each* Pikafish instance in MiB.").int().default(Pikafish.DEFAULT_HASH_SIZE_MIB)
    val nodesToSearchPerMove : Int by option("--nodes", help="Minimum number of nodes to search per move in each game.").int().restrictTo(1..Int.MAX_VALUE).default(GameReviewService.DEFAULT_NODES_TO_SEARCH_PER_MOVE)

    override fun run() {
        // guests have uncertain ratings and highly similar "usernames"; excluding guest games likely improves separation of anomalies by username similarity and rating
        val games = GameImportingService().importFromCSV(csvFilePath).filter { !it.blackPlayer.isGuest && !it.redPlayer.isGuest }
        val pikafishInstances = ArrayList<Pikafish>()
        (0 until pikafishPoolSize).forEach { _ -> pikafishInstances.add(Pikafish(pikafishExecutable, numThreads, hashSizeMiB)) }
        val pool = Channel<Pikafish>(pikafishInstances.size)
        pikafishInstances.forEach { pool.trySend(it) }

        suspend fun reviewGames(games: List<Game>) = coroutineScope {
            games.mapIndexed { i, game ->
                async(Dispatchers.Default) {
                    val pikafish = pool.receive()
                    val gameReviewService = GameReviewService(pikafish)
                    val reviewedGame = gameReviewService.review(game, nodesToSearchPerMove)
                    pikafish.clear()
                    pool.send(pikafish)
                    println("Reviewed game #$i / ${games.size}")
                    return@async reviewedGame
                }
            }
        }.awaitAll()

        val allReviewedGames = runBlocking { reviewGames(games) }
        val featureExtractionService = FeatureExtractionService()
        val allFeatures = buildList { addAll(allReviewedGames.map { featureExtractionService.getFeatures(it) }) }
        val encoder = Encoder()
        val data = allFeatures.map { encoder.encode(it) }.toTypedArray()
        val screeningModel = ScreeningModel().fit(data)
        val file = File("model.json")
        file.writeText(screeningModel.toJson())
    }
}

fun main(args : Array<String>) = ScreeningModelTrainer().main(args)