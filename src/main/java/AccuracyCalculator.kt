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
        val volatilities = computeVolatilityWeights(windows)
        val volatilityWeightedMeanAccuracy = computeVolatilityWeightedMeanAccuracy(windows, volatilities)
        val harmonicMeanAccuracy = computeHarmonicMeanAccuracy(reviewedMoves)
        val gameAccuracy = listOf(volatilityWeightedMeanAccuracy, harmonicMeanAccuracy).average()
        return gameAccuracy
    }

    fun window(reviewedMoves : List<ReviewedMove>) : List<List<ReviewedMove>> {
        val windowSize = (reviewedMoves.size / 10).coerceIn(MIN_WINDOW_SIZE..MAX_WINDOW_SIZE)
        return reviewedMoves.chunked(windowSize)
    }

    fun computeVolatilityWeights(windows : List<List<ReviewedMove>>) : List<Double> {
        return windows.map {
            val winPercents = it.map { reviewedMove -> reviewedMove.bestMoveEvaluation.winPercent }.toDoubleArray()
            val stdev = StandardDeviation()
            val rawVolatilityWeight = stdev.evaluate(winPercents)
            return@map rawVolatilityWeight.coerceIn(MIN_VOLATILITY_WEIGHT..MAX_VOLATILITY_WEIGHT)
        }
    }

    fun computeVolatilityWeightedMeanAccuracy(windows : List<List<ReviewedMove>>, volatilities : List<Double>) : Double {
        val weightedAccuracySum = windows.zip(volatilities).flatMap {
            (window, volatility) ->
            window.map {
                it.accuracyPercent * volatility
            }
        }.sum()
        val normalizingFactor = volatilities.sum()
        return weightedAccuracySum / normalizingFactor
    }

    fun computeHarmonicMeanAccuracy(reviewedMoves : List<ReviewedMove>) : Double {
        val reciprocalAccuracyPercents = reviewedMoves.map { 1.0 / it.accuracyPercent }
        val meanReciprocalAccuracyPercents = reciprocalAccuracyPercents.average()
        val harmonicMeanAccuracyPercent = 1.0 / meanReciprocalAccuracyPercents
        return harmonicMeanAccuracyPercent
    }

    companion object{
        const val MIN_WINDOW_SIZE = 2
        const val MAX_WINDOW_SIZE = 8
        const val MIN_VOLATILITY_WEIGHT = 0.5
        const val MAX_VOLATILITY_WEIGHT = 12.0
    }
}