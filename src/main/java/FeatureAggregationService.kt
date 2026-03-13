import org.apache.commons.text.similarity.JaroWinklerSimilarity

class FeatureAggregationService(val featureProviders : List<IFeatureProvider>) {

    fun getFeatures(reviewedGame: ReviewedGame, alliance: Alliance) : DoubleArray{
        val allFeatureMaps = featureProviders.map { it.extract(reviewedGame, alliance) }
        TODO()
    }

    private fun getEvaluationGraphRedPerspective(reviewedMoves: List<ReviewedMove>) : List<Evaluation> {
        val evaluationGraphRedPerspective = reviewedMoves
            .map(ReviewedMove::movePlayedEvaluation)
            .mapIndexed {
                    i, evaluation ->
                if(i.mod(2) == 0) evaluation else evaluation.flip()
            }
        return evaluationGraphRedPerspective
    }

    private fun getUsernameSimilarity(redUsername : String, blackUsername : String) : Double{
        val jwSimilarity = JaroWinklerSimilarity()
        return jwSimilarity.apply(redUsername, blackUsername)
    }

    private fun getLongestBestOrExcellentStreak(reviewedMovesForAlliance: List<ReviewedMove>) : Int {
        val streakQualities = setOf(MoveQuality.BEST, MoveQuality.EXCELLENT)
        return reviewedMovesForAlliance
            .map { if (it.moveQuality in streakQualities) 'S' else ' ' }
            .joinToString("")
            .split(" ")
            .maxOfOrNull { it.length } ?: 0
    }

    private fun getBlunderRate(reviewedMovesForAlliance: List<ReviewedMove>) : Double {
        return reviewedMovesForAlliance
            .count { it.moveQuality == MoveQuality.BLUNDER } / reviewedMovesForAlliance.size.toDouble()
    }

    private fun getAccuracy(reviewedMovesForAlliance: List<ReviewedMove>) : Double{
        return reviewedMovesForAlliance
            .count { it.moveQuality == MoveQuality.BEST} / reviewedMovesForAlliance.size.toDouble()
    }
}