import com.github.ajalt.clikt.core.CliktCommand
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.util.logging.Level
import java.util.logging.Logger

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
        val featureService = FeatureAggregationService(Feature.entries)

        embeddedServer(Netty, port = port) {
            install(ContentNegotiation) {
                jackson { enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT) }
            }
            routing {
                post("/screen-game") {
                    val pikafish = pool.receive()
                    try {
                        val game = call.receive<Game>()
                        val gameReviewService = GameReviewService(pikafish)
                        val reviewed = gameReviewService.review(game, nodesToSearchPerMove)
                        val redData = featureService.getFeatures(reviewed, Alliance.RED)
                        val blackData = featureService.getFeatures(reviewed, Alliance.BLACK)

                        val redAnomalyScore = model.predict(arrayOf(redData)).first()
                        val blackAnomalyScore = model.predict(arrayOf(blackData)).first()

                        call.respond(mapOf("status" to "success", "red_anomaly_score" to redAnomalyScore, "black_anomaly_score" to blackAnomalyScore))
                    } catch (e: Exception) {
                        log.log(Level.SEVERE, e.message)
                        call.respond(mapOf("status" to "failure", "message" to "Internal Server Error"))
                    } finally {
                        pool.send(pikafish)
                    }
                }
            }
        }.start(wait = true)
    }
    companion object {
        val log : Logger = Logger.getLogger(this::class.java.simpleName)
    }
}