import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File

val FEATURE_PROVIDERS = listOf(
    GameInfoFeatureExtractor(),
    AccuracyFeatureExtractor()
)

class Sapphira : CliktCommand() {
    override fun run() = Unit
}

class Trainer : CliktCommand() {
    val csvFilePath by argument("--data", help="Path to the CSV file of user games to train on. Must be formatted like the data sample!").file()
    val pikafishExecutable by argument("--exe", help="Path to Pikafish executable.").file()
    val pikafishPoolSize by option("--pool", help="Number of concurrent Pikafish instances that will be used for analysis.").int().restrictTo(1..Int.MAX_VALUE).default(1)
    val numThreads by option("--threads", help="Number of threads for EACH Pikafish instance in the pool.").int().default(Pikafish.DEFAULT_THREADS)
    val hashSizeMiB by option("--hash", help="Hash table size in MiB for EACH Pikafish instance in the pool.").int().default(Pikafish.DEFAULT_HASH_SIZE_MIB)
    val nodesToSearchPerMove by option("--nodes", help="Minimum number of leaf nodes to search in each position for analysis.").int().restrictTo(1..Int.MAX_VALUE).default(GameReviewService.DEFAULT_NODES_TO_SEARCH_PER_MOVE)

    override fun run() {
        val games = GameImportingService().importFromCSV(csvFilePath)
            .filter { !it.blackPlayer.isGuest && !it.redPlayer.isGuest }

        val pool = Channel<Pikafish>(pikafishPoolSize).apply {
            repeat(pikafishPoolSize) { runBlocking{ send(Pikafish(pikafishExecutable, numThreads, hashSizeMiB)) }}
        }

        val allReviewedGames = runBlocking {
            games.mapIndexed { i, game ->
                async(Dispatchers.Default) {
                    val pikafish = pool.receive()
                    val gameReviewService = GameReviewService(pikafish, MoveAccuracyCalculator(), MoveClassifier())
                    val reviewed = gameReviewService.review(game, nodesToSearchPerMove)
                    pikafish.clear()
                    pool.send(pikafish)
                    return@async reviewed
                }
            }.awaitAll()
        }

        val featureService = FeatureAggregationService(FEATURE_PROVIDERS)
        val data = allReviewedGames.map { featureService.getFeatures(it) }.toTypedArray()
        val screeningModel = ScreeningModel().fit(data)
        File("model.json").writeText(screeningModel.toJson())
    }
}

class Server : CliktCommand() {
    val pikafishExecutable by argument("--exe", help="Path to Pikafish executable.").file()
    val modelFile by argument("--model", help="Path to pretrained screening model. Run the trainer first if you don't have a model yet").file()
    val pikafishPoolSize by option("--pool", help="Number of concurrent Pikafish instances that will be used for analysis.").int().restrictTo(1..Int.MAX_VALUE).default(1)
    val numThreads by option("--threads", help="Number of threads for EACH Pikafish instance in the pool.").int().default(Pikafish.DEFAULT_THREADS)
    val hashSizeMiB by option("--hash", help="Hash table size in MiB for EACH Pikafish instance in the pool.").int().default(Pikafish.DEFAULT_HASH_SIZE_MIB)
    val nodesToSearchPerMove by option("--nodes", help="Minimum number of leaf nodes to search in each position for analysis.").int().restrictTo(1..Int.MAX_VALUE).default(GameReviewService.DEFAULT_NODES_TO_SEARCH_PER_MOVE)
    val port by option("--port", help="What port the webservice should use.").int().default(8080)

    override fun run() {
        val pool = Channel<Pikafish>(pikafishPoolSize).apply {
            repeat(pikafishPoolSize) { runBlocking { send(Pikafish(pikafishExecutable, numThreads, hashSizeMiB)) }}
        }

        val model = ScreeningModel.fromJson(modelFile.readText())
        val featureService = FeatureAggregationService(FEATURE_PROVIDERS)

        embeddedServer(Netty, port = port) {
            install(ContentNegotiation) {
                jackson { enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT) }
            }
            routing {
                post("/screen-game") {
                    val pikafish = pool.receive()
                    try {
                        val game = call.receive<Game>()
                        val gameReviewService = GameReviewService(pikafish, MoveAccuracyCalculator(), MoveClassifier())
                        val reviewed = gameReviewService.review(game, nodesToSearchPerMove)
                        val data = featureService.getFeatures(reviewed)
                        val score = model.predict(arrayOf(data)).firstOrNull() ?: Double.NaN

                        call.respond(mapOf("status" to "success", "anomaly_score" to score))
                    } catch (e: Exception) {
                        call.respond(mapOf("status" to "failure", "message" to (e.message ?: "Server Error")))
                    } finally {
                        pool.send(pikafish)
                    }
                }
            }
        }.start(wait = true)
    }
}

fun main(args: Array<String>) = Sapphira()
    .subcommands(Trainer(), Server())
    .main(args)