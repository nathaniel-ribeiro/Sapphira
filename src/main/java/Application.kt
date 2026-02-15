import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File
import kotlin.collections.mapOf

data class Response(val status : String, val anomalyScore : Double, val isAnomalous : Boolean)

class Application : CliktCommand() {
    val pikafishExecutable : File by argument("--exe", help="Path to the Pikafish executable.").file()
    val modelFile : File by argument("--model", help="Path to trained screening model.").file()
    val numThreads : Int by option("--threads", help="Number of threads used for *each* Pikafish instance.").int().default(Pikafish.DEFAULT_THREADS)
    val hashSizeMiB : Int by option("--hash", help="Hash size for *each* Pikafish instance in MiB.").int().default(Pikafish.DEFAULT_HASH_SIZE_MIB)
    val nodesToSearchPerMove : Int by option("--nodes", help="Minimum number of nodes to search per move in each game.").int().restrictTo(1..Int.MAX_VALUE).default(GameReviewService.DEFAULT_NODES_TO_SEARCH_PER_MOVE)
    val port by option("--port").int().default(8080)

    override fun run() {
        val model = ScreeningModel.fromJson(modelFile.readText())
        val pikafish = Pikafish(pikafishExecutable, numThreads, hashSizeMiB)
        startWebService(model, pikafish, nodesToSearchPerMove, port)
    }
}

fun startWebService(model : ScreeningModel, pikafish : Pikafish, nodesToSearchPerMove : Int, port : Int){
    val reviewService = GameReviewService(pikafish)
    val featureService = FeatureExtractionService()
    val encoder = Encoder()

    val server = embeddedServer(Netty, port = port) {
        install(ContentNegotiation) {
            jackson {
                enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
            }
        }

        routing {
            post("/screen-game") {
                try {
                    val game = call.receive<Game>()
                    val reviewedGame = reviewService.review(game, nodesToSearchPerMove)
                    val features = featureService.getFeatures(reviewedGame)
                    val encoded = encoder.encode(features)
                    val scores = model.predict(arrayOf(encoded))
                    val score = scores.firstOrNull() ?: 0.0

                    call.respond(mapOf("status" to "success", "score" to score, "anomaly" to (score >= 0.50)))
                } catch (_: Exception) {
                    call.respond(mapOf("status" to "failure", "message" to "An internal server error occurred."))
                }
            }
        }
    }
    server.start(wait = true)
}

fun main(args : Array<String>) = Application().main(args)