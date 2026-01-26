import org.apache.commons.text.similarity.JaroWinklerSimilarity
import kotlin.math.abs

class FeatureExtractionService(val options: FeatureExtractionOptions) {

    fun getUsernameSimilarity(reviewedGame: ReviewedGame) : Double{
        val redUsername = reviewedGame.game.redPlayer.username
        val blackUsername = reviewedGame.game.blackPlayer.username
        val jwSimilarity = JaroWinklerSimilarity()
        return jwSimilarity.apply(redUsername, blackUsername)
    }

    fun getAdjustedCPLoss(reviewedGame : ReviewedGame, alliance: Alliance) : Double?{
        val adjustedAllianceMoves = reviewedGame.reviewedMovesFor(alliance)
                                                .filterIndexed { index, _ ->  index >= options.numberOfTurnsToExclude}
                                                .filter {
                                                    abs(it.bestMoveEvaluation.centipawns) <= options.winningAdvantageThreshold &&
                                                    abs(it.movePlayedEvaluation.centipawns) <= options.winningAdvantageThreshold
                                                }
                                                .filter {
                                                    (it.bestMoveEvaluation.winProbability <= 0.90 && it.bestMoveEvaluation.flip().winProbability <= 0.90) &&
                                                    (it.movePlayedEvaluation.winProbability <= 0.90 && it.movePlayedEvaluation.flip().winProbability <= 0.90)
                                                }

        if(adjustedAllianceMoves.isEmpty()) return null
        return adjustedAllianceMoves.map(ReviewedMove::centipawnLoss).average()
    }

    fun getLongestBestOrExcellentStreak(reviewedGame : ReviewedGame, alliance : Alliance) : Int {
        val allianceMoves = reviewedGame.reviewedMovesFor(alliance)
        val streakQualities = setOf(MoveQuality.BEST, MoveQuality.EXCELLENT)
        return allianceMoves
            .map { if (it.moveQuality in streakQualities) 'S' else ' ' }
            .joinToString("")
            .split(" ")
            .maxOfOrNull { it.length } ?: 0
    }

    fun getBlunderRate(reviewedGame : ReviewedGame, alliance : Alliance) : Double {
        val allianceMoves = reviewedGame.reviewedMovesFor(alliance)
        return allianceMoves.count { it.moveQuality == MoveQuality.BLUNDER } / allianceMoves.size.toDouble()
    }

    fun getAverageBlunderInterarrivalTime(reviewedGame: ReviewedGame, alliance: Alliance) : Double {
        val allianceMoves = reviewedGame.reviewedMovesFor(alliance)
        val moveQualities = allianceMoves.map(ReviewedMove::moveQuality)
        val blunderMoveNumbers = moveQualities.mapIndexedNotNull { index, quality ->
            if (quality == MoveQuality.BLUNDER) index + 1 else null
        }
        val gameStart = 0
        val gameEnd = moveQualities.size
        // NOTE: forcing a pseudo-blunder at the end is not statistically rigorous but ensures we have
        // *some* lower bound on blunder rates for players who didn't blunder at all during their game
        val timeline = listOf(gameStart) + blunderMoveNumbers + listOf(gameEnd)
        return timeline
            .zipWithNext { a, b -> b - a }
            .average()
    }

    fun getTimeSeriesFeatures(reviewedGame : ReviewedGame, alliance : Alliance){
        val evaluationGraphRedPerspective = reviewedGame.reviewedMoves
                                                        .map(ReviewedMove::movePlayedEvaluation)
                                                        .mapIndexed {
                                                            i, evaluation ->
                                                            if(i.mod(2) == 0) evaluation else evaluation.flip()
                                                        }
        TODO()
    }

    fun getAccuracy(reviewedGame: ReviewedGame, alliance: Alliance) : Double{
        val allianceMoves = reviewedGame.reviewedMovesFor(alliance)
        return allianceMoves.count { it.moveQuality == MoveQuality.BEST} / allianceMoves.size.toDouble()
    }

    fun getTotalPlies(reviewedGame: ReviewedGame) : Int{
        return reviewedGame.reviewedMoves.size
    }

    companion object{
        private fun ReviewedGame.reviewedMovesFor(alliance: Alliance) : List<ReviewedMove> =  reviewedMoves.filter { it.movePlayed.whoMoved == alliance }
    }
}