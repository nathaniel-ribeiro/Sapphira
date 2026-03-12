import org.apache.commons.math3.analysis.integration.SimpsonIntegrator
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import kotlin.math.abs

class FeatureAggregationService(val featureProviders : List<IFeatureProvider>) {

    fun getFeatures(reviewedGame: ReviewedGame) : DoubleArray{
        val allFeatureMaps = featureProviders.map { it.extract(reviewedGame) }
        TODO()
    }

//    private fun getAUC(evaluationGraph: List<Evaluation>) : Double? {
//        if (evaluationGraph.size < 5) return null
//        val x = DoubleArray(evaluationGraph.size) { it.toDouble() }
//        val y = DoubleArray(evaluationGraph.size) { evaluationGraph[it].expectedScore }
//        val interpolator = SplineInterpolator()
//        val spline = interpolator.interpolate(x, y)
//        val integrator = SimpsonIntegrator()
//        return integrator.integrate(10_000, spline, x.first(), x.last())
//    }

    private fun getEvaluationGraphRedPerspective(reviewedMoves: List<ReviewedMove>) : List<Evaluation> {
        val evaluationGraphRedPerspective = reviewedMoves
            .map(ReviewedMove::movePlayedEvaluation)
            .mapIndexed {
                    i, evaluation ->
                if(i.mod(2) == 0) evaluation else evaluation.flip()
            }
        return evaluationGraphRedPerspective
    }

    private fun getNumReversals(evaluationGraph: List<Evaluation>): Int {
        return evaluationGraph
            .map { eval ->
                when {
                    eval.centipawns > WINNING_ADVANTAGE_CENTIPAWNS -> "RED_WINNING"
                    eval.centipawns < -WINNING_ADVANTAGE_CENTIPAWNS -> "RED_LOSING"
                    else -> "ROUGHLY_EQUAL"
                }
            }
            .filter { it != "ROUGHLY_EQUAL" }
            .zipWithNext()
            .count { (current, next) -> current != next }
    }

    private fun getRecoveryRate(reviewedMovesForAlliance: List<ReviewedMove>) : Double? {
        // what fraction of the time was a MISTAKE or BLUNDER followed by the BEST or an EXCELLENT move?
        if(reviewedMovesForAlliance.size < 2) return null
        val recoveryOpportunities = reviewedMovesForAlliance.windowed(2)
            .filter { (current, _) ->
                current.moveQuality == MoveQuality.MISTAKE || current.moveQuality == MoveQuality.BLUNDER
            }
        if (recoveryOpportunities.isEmpty()) return null
        val successfulRecoveries = recoveryOpportunities
            .count { (_, next) ->
                next.moveQuality == MoveQuality.BEST || next.moveQuality == MoveQuality.EXCELLENT
            }
        return successfulRecoveries.toDouble() / recoveryOpportunities.size
    }

    private fun getUsernameSimilarity(redUsername : String, blackUsername : String) : Double{
        val jwSimilarity = JaroWinklerSimilarity()
        return jwSimilarity.apply(redUsername, blackUsername)
    }

//    private fun getAdjustedCPLoss(reviewedMovesForAlliance : List<ReviewedMove>) : Double?{
//        val adjustedAllianceMoves = reviewedMovesForAlliance
//            .filterIndexed { index, _ ->  index >= NUMBER_OF_TURNS_TO_EXCLUDE}
//            .filter {
//                abs(it.bestMoveEvaluation.centipawns) <= WINNING_ADVANTAGE_CENTIPAWNS &&
//                        abs(it.movePlayedEvaluation.centipawns) <= WINNING_ADVANTAGE_CENTIPAWNS
//            }
//            .filter {
//                it.bestMoveEvaluation.winProbability <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
//                        it.bestMoveEvaluation.flip().winProbability <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
//                        it.movePlayedEvaluation.winProbability <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
//                        it.movePlayedEvaluation.flip().winProbability <= WINNING_ADVANTAGE_WIN_PROBABILITY
//            }
//
//        if(adjustedAllianceMoves.isEmpty()) return null
//        return adjustedAllianceMoves.map(ReviewedMove::centipawnLoss).average()
//    }

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

    private fun getAverageBlunderInterarrivalTime(reviewedMovesForAlliance: List<ReviewedMove>) : Double {
        val moveQualities = reviewedMovesForAlliance.map(ReviewedMove::moveQuality)
        val blunderMoveNumbers = moveQualities.mapIndexedNotNull { index, quality ->
            if (quality == MoveQuality.BLUNDER) index + 1 else null
        }
        val gameStart = 0
        val gameEnd = moveQualities.size
        // NOTE: forcing a pseudo-blunder at the end is not statistically rigorous but ensures we have
        // *some* lower bound on blunder rates for players who didn't blunder at all during their game
        val timeline = listOf(gameStart) + blunderMoveNumbers + listOf(gameEnd)
        return timeline.zipWithNext { a, b -> b - a }.average()
    }

    private fun getAccuracy(reviewedMovesForAlliance: List<ReviewedMove>) : Double{
        return reviewedMovesForAlliance
            .count { it.moveQuality == MoveQuality.BEST} / reviewedMovesForAlliance.size.toDouble()
    }

    companion object{
        const val NUMBER_OF_TURNS_TO_EXCLUDE = 7
        const val WINNING_ADVANTAGE_CENTIPAWNS = 300
        const val WINNING_ADVANTAGE_WIN_PROBABILITY = 0.90
    }
}