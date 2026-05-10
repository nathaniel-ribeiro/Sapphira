import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

class Trainer : CliktCommand() {
    val csvFilePath by argument("--data", help="Path to the CSV file of user games to train on. Must be formatted like the data sample!").file()
    val pikafishExecutable by argument("--exe", help="Path to Pikafish executable.").file()
    val pikafishPoolSize by option("--pool", help="Number of concurrent Pikafish instances that will be used for analysis.").int().restrictTo(1..Int.MAX_VALUE).default(1)
    val numThreads by option("--threads", help="Number of threads for EACH Pikafish instance in the pool.").int().restrictTo(Pikafish.MIN_THREADS..Pikafish.MAX_THREADS).default(Pikafish.DEFAULT_THREADS)
    val hashSizeMiB by option("--hash", help="Hash table size in MiB for EACH Pikafish instance in the pool.").int().restrictTo(Pikafish.MIN_HASH_SIZE_MIB..Pikafish.MAX_HASH_SIZE_MIB).default(Pikafish.DEFAULT_HASH_SIZE_MIB)
    val nodesToSearchPerMove by option("--nodes", help="Minimum number of leaf nodes to search in each position for analysis.").int().restrictTo(1..Int.MAX_VALUE).default(GameReviewService.DEFAULT_NODES_TO_SEARCH_PER_MOVE)

    override fun run() {
        var games = GameImportingService().importFromCSV(csvFilePath)
        val oldTrainingDataCount = games.size
        val trainingDataCleaningService = TrainingDataCleaningService()
        games = games.filter { trainingDataCleaningService.shouldRetain(it) }
        val newTrainingDataCount = games.size

        log.log(Level.INFO, "Training data pruned from $oldTrainingDataCount examples to $newTrainingDataCount examples")

        val pool = Channel<Pikafish>(pikafishPoolSize).apply {
            repeat(pikafishPoolSize) { runBlocking{ send(Pikafish(pikafishExecutable, numThreads, hashSizeMiB)) }}
        }

        val reviewedGames = runBlocking {
            games.mapIndexed { i, game ->
                async(Dispatchers.Default) {
                    val pikafish = pool.receive()
                    val gameReviewService = GameReviewService(pikafish)
                    val reviewed = gameReviewService.review(game, nodesToSearchPerMove)
                    pikafish.clear()
                    pool.send(pikafish)
                    log.log(Level.FINEST, "Finished processing game #$i of $newTrainingDataCount")
                    return@async reviewed
                }
            }.awaitAll()
        }

        val featureService = FeatureAggregationService(Features.entries)
        val redData = reviewedGames.map { featureService.getFeatures(it, Alliance.RED) }.toTypedArray()
        val blackData = reviewedGames.map { featureService.getFeatures(it, Alliance.BLACK) }.toTypedArray()
        val data = redData.plus(blackData)

        val dataExportService = DataExportService()
        val headers = featureService.featureNames
        dataExportService.saveToCsv("processed_training_data.csv", headers, data)
        log.log(Level.INFO, "Saved training data as CSV")

        val screeningModel = ScreeningModel().fit(data)
        File("model.json").writeText(screeningModel.toJson())
        log.log(Level.INFO, "Saved trained screening model to JSON file")
    }
    companion object {
        val log : Logger = Logger.getLogger(this::class.java.simpleName)
    }
}