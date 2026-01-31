import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import kotlin.math.abs

class FeatureExtractionService(val options: FeatureExtractionOptions) {

    fun getFeatures(reviewedGame: ReviewedGame, redThinkTimes : List<Int>, blackThinkTimes : List<Int>) : Features{
        val usernameSimilarity = getUsernameSimilarity(reviewedGame.game.redPlayer.username, reviewedGame.game.blackPlayer.username)
        val adjustedCPLossRed = getAdjustedCPLoss(reviewedGame, Alliance.RED)
        val adjustedCPLossBlack = getAdjustedCPLoss(reviewedGame, Alliance.BLACK)
        val longestBestOrExcellentStreakRed = getLongestBestOrExcellentStreak(reviewedGame, Alliance.RED)
        val longestBestOrExcellentStreakBlack = getLongestBestOrExcellentStreak(reviewedGame, Alliance.BLACK)
        val blunderRateRed = getBlunderRate(reviewedGame, Alliance.RED)
        val blunderRateBlack = getBlunderRate(reviewedGame, Alliance.BLACK)
        val averageBlunderInterarrivalTimeRed = getAverageBlunderInterarrivalTime(reviewedGame, Alliance.RED)
        val averageBlunderInterarrivalTimeBlack = getAverageBlunderInterarrivalTime(reviewedGame, Alliance.BLACK)
        // TODO: time series features
        val accuracyRed = getAccuracy(reviewedGame, Alliance.RED)
        val accuracyBlack = getAccuracy(reviewedGame, Alliance.BLACK)
        val gameLength = getGameLength(reviewedGame)
        val averageThinkTimeRed = getAverageThinkTime(redThinkTimes)
        val averageThinkTimeBlack = getAverageThinkTime(blackThinkTimes)
        val stdevThinkTimeRed = getThinkTimeStdev(redThinkTimes)
        val stdevThinkTimeBlack = getThinkTimeStdev(blackThinkTimes)

        return Features(usernameSimilarity,
                        adjustedCPLossRed,
                        adjustedCPLossBlack,
                        longestBestOrExcellentStreakRed,
                        longestBestOrExcellentStreakBlack,
                        blunderRateRed,
                        blunderRateBlack,
                        averageBlunderInterarrivalTimeRed,
                        averageBlunderInterarrivalTimeBlack,
                        accuracyRed,
                        accuracyBlack,
                        gameLength,
                        averageThinkTimeRed,
                        averageThinkTimeBlack,
                        stdevThinkTimeRed,
                        stdevThinkTimeBlack
                )
    }

    private fun getUsernameSimilarity(redUsername : String, blackUsername : String) : Double{
        val jwSimilarity = JaroWinklerSimilarity()
        return jwSimilarity.apply(redUsername, blackUsername)
    }

    private fun getAdjustedCPLoss(reviewedGame : ReviewedGame, alliance: Alliance) : Double?{
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

    private fun getLongestBestOrExcellentStreak(reviewedGame : ReviewedGame, alliance : Alliance) : Int {
        val allianceMoves = reviewedGame.reviewedMovesFor(alliance)
        val streakQualities = setOf(MoveQuality.BEST, MoveQuality.EXCELLENT)
        return allianceMoves
            .map { if (it.moveQuality in streakQualities) 'S' else ' ' }
            .joinToString("")
            .split(" ")
            .maxOfOrNull { it.length } ?: 0
    }

    private fun getBlunderRate(reviewedGame : ReviewedGame, alliance : Alliance) : Double {
        val allianceMoves = reviewedGame.reviewedMovesFor(alliance)
        return allianceMoves.count { it.moveQuality == MoveQuality.BLUNDER } / allianceMoves.size.toDouble()
    }

    private fun getAverageBlunderInterarrivalTime(reviewedGame: ReviewedGame, alliance: Alliance) : Double {
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
        return timeline.zipWithNext { a, b -> b - a }.average()
    }

    private fun getTimeSeriesFeatures(reviewedGame : ReviewedGame, alliance : Alliance){
        val evaluationGraphRedPerspective = reviewedGame.reviewedMoves
                                                        .map(ReviewedMove::movePlayedEvaluation)
                                                        .mapIndexed {
                                                            i, evaluation ->
                                                            if(i.mod(2) == 0) evaluation else evaluation.flip()
                                                        }
        TODO()
    }

    private fun getAccuracy(reviewedGame: ReviewedGame, alliance: Alliance) : Double{
        val allianceMoves = reviewedGame.reviewedMovesFor(alliance)
        return allianceMoves.count { it.moveQuality == MoveQuality.BEST} / allianceMoves.size.toDouble()
    }

    private fun getGameLength(reviewedGame: ReviewedGame) : Int{
        return reviewedGame.reviewedMoves.size
    }

    private fun getAverageThinkTime(thinkTimes : List<Int>) : Double {
        return thinkTimes.average()
    }

    private fun getThinkTimeStdev(thinkTimes : List<Int>) : Double {
        val stdev = StandardDeviation()
        return stdev.evaluate(thinkTimes.map { it.toDouble() }.toDoubleArray())
    }

    companion object{
        private fun ReviewedGame.reviewedMovesFor(alliance: Alliance) : List<ReviewedMove> =  reviewedMoves.filter { it.movePlayed.whoMoved == alliance }
    }
}