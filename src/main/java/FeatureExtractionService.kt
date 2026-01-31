import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import kotlin.math.abs

class FeatureExtractionService {

    fun getFeatures(reviewedGame: ReviewedGame, redThinkTimes : List<Int>, blackThinkTimes : List<Int>) : Features{
        val reviewedMovesRed = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == Alliance.RED }
        val reviewedMovesBlack = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == Alliance.BLACK }

        // general features about the game
        //TODO: probably refactor this, getFeatures is doing a lot
        val gameTimer = reviewedGame.game.gameTimer
        val moveTimer = reviewedGame.game.moveTimer
        val increment = reviewedGame.game.increment
        val resultRed = reviewedGame.game.resultRed
        val resultBlack = reviewedGame.game.resultBlack
        val gameResultReason = reviewedGame.game.gameResultReason
        val usernameSimilarity = getUsernameSimilarity(reviewedGame.game.redPlayer.username, reviewedGame.game.blackPlayer.username)
        val gameLength = reviewedGame.game.moves.size

        // specific features about red/black's behavior and play quality
        // TODO: probably refactor this, getFeatures is doing a lot
        val adjustedCPLossRed = getAdjustedCPLoss(reviewedMovesRed)
        val adjustedCPLossBlack = getAdjustedCPLoss(reviewedMovesBlack)
        val longestBestOrExcellentStreakRed = getLongestBestOrExcellentStreak(reviewedMovesRed)
        val longestBestOrExcellentStreakBlack = getLongestBestOrExcellentStreak(reviewedMovesBlack)
        val blunderRateRed = getBlunderRate(reviewedMovesRed)
        val blunderRateBlack = getBlunderRate(reviewedMovesBlack)
        val averageBlunderInterarrivalTimeRed = getAverageBlunderInterarrivalTime(reviewedMovesRed)
        val averageBlunderInterarrivalTimeBlack = getAverageBlunderInterarrivalTime(reviewedMovesBlack)
        // TODO: time series features
        val accuracyRed = getAccuracy(reviewedMovesRed)
        val accuracyBlack = getAccuracy(reviewedMovesBlack)
        val averageThinkTimeRed = redThinkTimes.average()
        val averageThinkTimeBlack = blackThinkTimes.average()
        val stdevThinkTimeRed = getThinkTimeStdev(redThinkTimes)
        val stdevThinkTimeBlack = getThinkTimeStdev(blackThinkTimes)

        return Features(gameTimer,
                        moveTimer,
                        increment,
                        resultRed,
                        resultBlack,
                        gameResultReason,
                        usernameSimilarity,
                        adjustedCPLossRed ?: Double.NaN,
                        adjustedCPLossBlack ?: Double.NaN,
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
                        stdevThinkTimeBlack)
    }

    private fun getUsernameSimilarity(redUsername : String, blackUsername : String) : Double{
        val jwSimilarity = JaroWinklerSimilarity()
        return jwSimilarity.apply(redUsername, blackUsername)
    }

    private fun getAdjustedCPLoss(reviewedMovesForAlliance : List<ReviewedMove>) : Double?{
        val adjustedAllianceMoves = reviewedMovesForAlliance
                                                .filterIndexed { index, _ ->  index >= NUMBER_OF_TURNS_TO_EXCLUDE}
                                                .filter {
                                                    abs(it.bestMoveEvaluation.centipawns) <= WINNING_ADVANTAGE_CENTIPAWNS &&
                                                    abs(it.movePlayedEvaluation.centipawns) <= WINNING_ADVANTAGE_CENTIPAWNS
                                                }
                                                .filter {
                                                    it.bestMoveEvaluation.winProbability <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
                                                    it.bestMoveEvaluation.flip().winProbability <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
                                                    it.movePlayedEvaluation.winProbability <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
                                                    it.movePlayedEvaluation.flip().winProbability <= WINNING_ADVANTAGE_WIN_PROBABILITY
                                                }

        if(adjustedAllianceMoves.isEmpty()) return null
        return adjustedAllianceMoves.map(ReviewedMove::centipawnLoss).average()
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

    private fun getTimeSeriesFeatures(reviewedGame : ReviewedGame){
        val evaluationGraphRedPerspective = reviewedGame.reviewedMoves
                                                        .map(ReviewedMove::movePlayedEvaluation)
                                                        .mapIndexed {
                                                            i, evaluation ->
                                                            if(i.mod(2) == 0) evaluation else evaluation.flip()
                                                        }
        TODO()
    }

    private fun getAccuracy(reviewedMovesForAlliance: List<ReviewedMove>) : Double{
        return reviewedMovesForAlliance
                .count { it.moveQuality == MoveQuality.BEST} / reviewedMovesForAlliance.size.toDouble()
    }

    private fun getThinkTimeStdev(thinkTimes : List<Int>) : Double {
        val stdev = StandardDeviation()
        return stdev.evaluate(thinkTimes.map { it.toDouble() }.toDoubleArray())
    }

    companion object{
        const val NUMBER_OF_TURNS_TO_EXCLUDE = 7
        const val WINNING_ADVANTAGE_CENTIPAWNS = 300
        const val WINNING_ADVANTAGE_WIN_PROBABILITY = 0.90
    }
}