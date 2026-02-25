import org.apache.commons.math3.stat.descriptive.moment.Mean
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation

class AccuracyCalculator : FeatureProvider {
    override fun extract(reviewedGame: ReviewedGame): Map<String, Double?> {
        val redMoves = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == Alliance.RED }
        val blackMoves = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == Alliance.BLACK }
        val redAccuracy = getAccuracy(redMoves)
        val blackAccuracy = getAccuracy(blackMoves)
        return mapOf("Red Accuracy" to redAccuracy, "Black Accuracy" to blackAccuracy)
    }

    fun getAccuracy(reviewedMoves : List<ReviewedMove>) : Double {
        val windows = window(reviewedMoves)
        val volatilityPerWindow = computeVolatilityPerWindow(windows)
        val volatilityWeightedMeanAccuracy = computeVolatilityWeightedMeanAccuracy(windows, volatilityPerWindow)
        val harmonicMeanAccuracy = computeHarmonicMeanAccuracy(reviewedMoves)
        val gameAccuracy = listOf(volatilityWeightedMeanAccuracy, harmonicMeanAccuracy).average()
        return gameAccuracy
    }

    fun window(reviewedMoves : List<ReviewedMove>) : List<List<ReviewedMove>> {
        val windowSize = (reviewedMoves.size / TARGET_NUM_WINDOWS).coerceIn(MIN_WINDOW_SIZE..MAX_WINDOW_SIZE)
        return reviewedMoves.chunked(windowSize)
    }

    fun computeVolatilityPerWindow(windows : List<List<ReviewedMove>>) : List<Double> {
        return windows.map {
            val winPercents = it.map { reviewedMove -> reviewedMove.bestMoveEvaluation.winPercent }.toDoubleArray()
            val standardDeviation = StandardDeviation()
            val rawVolatility = standardDeviation.evaluate(winPercents)
            return@map rawVolatility.coerceIn(MIN_VOLATILITY..MAX_VOLATILITY)
        }
    }

    fun computeVolatilityWeightedMeanAccuracy(windows : List<List<ReviewedMove>>, volatilityPerWindow : List<Double>) : Double {
        val volatilityPerMove = windows.zip(volatilityPerWindow).flatMap {
            (window, volatility) -> List(window.size) { volatility }
        }
        val accuracyPercents = windows.flatten().map { it.accuracyPercent }
        val mean = Mean()
        return mean.evaluate(accuracyPercents.toDoubleArray(), volatilityPerMove.toDoubleArray())
    }

    fun computeHarmonicMeanAccuracy(reviewedMoves : List<ReviewedMove>) : Double {
        require(reviewedMoves.map { it.accuracyPercent }.all { it != 0.0 }){ "One or more accuracies was 0. Cannot take harmonic mean." }
        val reciprocalAccuracyPercents = reviewedMoves.map { 1.0 / it.accuracyPercent }
        val meanReciprocalAccuracyPercents = reciprocalAccuracyPercents.average()
        val harmonicMeanAccuracyPercent = 1.0 / meanReciprocalAccuracyPercents
        return harmonicMeanAccuracyPercent
    }

    companion object{
        const val MIN_WINDOW_SIZE = 2
        const val MAX_WINDOW_SIZE = 8
        const val MIN_VOLATILITY = 0.5
        const val MAX_VOLATILITY = 12.0
        const val TARGET_NUM_WINDOWS = 10
    }
}