class AccuracyCalculationService {
    fun getGameAccuracy(reviewedGame: ReviewedGame) : Double {
        val windows = window(reviewedGame.reviewedMoves)
        val volatilities = computeVolatilities(windows)
        val volatilityWeightedMeanAccuracy = computeVolatilityWeightedMeanAccuracy(windows, volatilities)
        val harmonicMeanAccuracy = computeHarmonicMeanAccuracy(reviewedGame.reviewedMoves)
        return listOf(volatilityWeightedMeanAccuracy, harmonicMeanAccuracy).average()
    }

    fun window(reviewedMoves : List<ReviewedMove>) : List<List<ReviewedMove>> {
        val windowSize = (reviewedMoves.size / 10).coerceIn(MIN_WINDOW_SIZE..MAX_WINDOW_SIZE)
        TODO("Divide the game into equal size windows")
    }

    fun computeVolatilities(windows : List<List<ReviewedMove>>) : List<Double> {
        TODO()
    }

    fun computeVolatilityWeightedMeanAccuracy(windows : List<List<ReviewedMove>>, volatilities : List<Double>) : Double {
        TODO()
    }

    fun computeHarmonicMeanAccuracy(reviewedMoves : List<ReviewedMove>) : Double {
        TODO()
    }

    companion object{
        const val MIN_WINDOW_SIZE = 2
        const val MAX_WINDOW_SIZE = 8
    }
}