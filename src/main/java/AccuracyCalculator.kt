import org.apache.commons.math3.stat.descriptive.moment.Mean
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import kotlin.math.exp

class AccuracyCalculator : FeatureProvider {
    fun ReviewedMove.accuracyPercent() : Double {
        if(movePlayedEvaluation >= bestMoveEvaluation) {
            return MAX_ACCURACY_PERCENT
        }
        else {
            val rawAccuracy = 103.1668 * exp(-0.04354 * (bestMoveEvaluation.winPercent - movePlayedEvaluation.winPercent)) - 3.1669
            return (rawAccuracy + UNCERTAINTY_BONUS).coerceIn(MIN_ACCURACY_PERCENT..MAX_ACCURACY_PERCENT)
        }
    }
    // TODO: consult Lichess source code for whether accuracies are computed entirely independently for the two colors or if windows include moves for BOTH colors
    override fun extract(reviewedGame: ReviewedGame): Map<String, Double?> {
        val accuracies = getAccuracy(reviewedGame.reviewedMoves)
        val redAccuracy = accuracies.first
        val blackAccuracy = accuracies.second
        return mapOf("Red Accuracy" to redAccuracy, "Black Accuracy" to blackAccuracy)
    }

    fun getAccuracy(reviewedMoves : List<ReviewedMove>) : Pair<Double, Double> {
        val windows = window(reviewedMoves)
        val volatilityPerWindow = computeVolatilityPerWindow(windows)
        val volatilityWeightedMeanAccuracy = computeVolatilityWeightedMeanAccuracy(windows, volatilityPerWindow)
        val harmonicMeanAccuracy = harmonicMean(reviewedMoves.map { it.accuracyPercent() })
        val gameAccuracy = (volatilityWeightedMeanAccuracy + harmonicMeanAccuracy) / 2
        return Pair(gameAccuracy, gameAccuracy)
    }

    fun getWinPercentageGraphRed(reviewedMoves: List<ReviewedMove>) : List<Double> {
        return reviewedMoves.map {
            if(it.movePlayed.whoMoved == Alliance.BLACK) it.movePlayedEvaluation.flip().winPercent
            else it.movePlayedEvaluation.winPercent
        }
    }

    fun window(reviewedMoves : List<ReviewedMove>) : List<List<ReviewedMove>> {
        val windowSize = (reviewedMoves.size / TARGET_NUM_WINDOWS).coerceIn(MIN_WINDOW_SIZE..MAX_WINDOW_SIZE)
        return reviewedMoves.chunked(windowSize)
    }

    fun computeVolatilityPerWindow(windows : List<List<ReviewedMove>>) : List<Double> {
        return windows.map {
            val winPercents = getWinPercentageGraphRed(it).toDoubleArray()
            val standardDeviation = StandardDeviation()
            val rawVolatility = standardDeviation.evaluate(winPercents)
            return@map rawVolatility.coerceIn(MIN_VOLATILITY..MAX_VOLATILITY)
        }
    }

    fun computeVolatilityWeightedMeanAccuracy(windows : List<List<ReviewedMove>>, volatilityPerWindow : List<Double>) : Double {
        val volatilityPerMove = windows.zip(volatilityPerWindow).flatMap {
            (window, volatility) -> List(window.size) { volatility }
        }
        val accuracyPercents = windows.flatten().map { it.accuracyPercent() }
        val mean = Mean()
        return mean.evaluate(accuracyPercents.toDoubleArray(), volatilityPerMove.toDoubleArray())
    }

    fun harmonicMean(values : List<Double>) : Double {
        // translated from https://github.com/lichess-org/scalalib/blob/master/lila/src/main/scala/Maths.scala
        return values.size / values.fold(0.0) { acc, v -> acc + 1 / maxOf(1.0, v) }
    }

    companion object{
        const val MIN_WINDOW_SIZE = 2
        const val MAX_WINDOW_SIZE = 8
        const val MIN_VOLATILITY = 0.5
        const val MAX_VOLATILITY = 12.0
        const val TARGET_NUM_WINDOWS = 10
        const val MIN_ACCURACY_PERCENT = 1.0
        const val MAX_ACCURACY_PERCENT = 100.0
        const val UNCERTAINTY_BONUS = 1.0
    }
}