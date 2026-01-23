import kotlin.math.abs

class FeatureExtractionService(val options: FeatureExtractionOptions) {
    fun getAdjustedCPLoss(reviewedGame : ReviewedGame, alliance: Alliance) : List<Int>{
        TODO()
    }

    fun getMoveQualityFrequencies(reviewedGame : ReviewedGame, alliance : Alliance) : Map<MoveQuality, Int>{
        TODO()
    }

    fun getLongestBestOrExcellentStreak(reviewedGame : ReviewedGame, alliance : Alliance) : Int {
        TODO()
    }

    fun getBlunderRate(reviewedGame : ReviewedGame, alliance : Alliance) : Double {
        TODO()
    }

    fun getTimeSeriesFeatures(reviewedGame : ReviewedGame, alliance : Alliance){
        TODO()
    }
}