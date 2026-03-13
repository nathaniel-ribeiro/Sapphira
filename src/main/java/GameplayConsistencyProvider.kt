import kotlin.math.abs

class GameplayConsistencyProvider : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Map<String, Double?> {
        val reviewedMovesForAlliance = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == alliance }
        val recoveryRate = getRecoveryRate(reviewedMovesForAlliance)
        val adjustedCPLoss = getAdjustedCPLoss(reviewedMovesForAlliance)
        val blunderRate = getBlunderRate(reviewedMovesForAlliance)
        val averageBlunderInterArrivalTime = getAverageBlunderInterarrivalTime(reviewedMovesForAlliance)
        val longestStreakBestOrExcellentPastOpening = getLongestStreakBestOrExcellentPastOpening(reviewedMovesForAlliance)
        return mapOf(
            "Recovery Rate" to recoveryRate,
            "Adjusted Centipawn Loss" to adjustedCPLoss,
            "Blunder Rate" to blunderRate,
            "Average Blunder Inter-Arrival Time" to averageBlunderInterArrivalTime,
            "Longest Streak of Best/Excellent Moves Past Opening" to longestStreakBestOrExcellentPastOpening.toDouble(),
        )
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

    private fun getAdjustedCPLoss(reviewedMovesForAlliance : List<ReviewedMove>) : Double?{
        val adjustedAllianceMoves = reviewedMovesForAlliance
            .filterIndexed { index, _ ->  index >= NUMBER_OF_TURNS_TO_EXCLUDE}
            .filter {
                abs(it.bestMoveEvaluation.centipawns) <= WINNING_ADVANTAGE_CENTIPAWNS &&
                abs(it.movePlayedEvaluation.centipawns) <= WINNING_ADVANTAGE_CENTIPAWNS
            }
            .filter {
                it.bestMoveEvaluation.winPercent <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
                it.bestMoveEvaluation.flip().winPercent <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
                it.movePlayedEvaluation.winPercent <= WINNING_ADVANTAGE_WIN_PROBABILITY &&
                it.movePlayedEvaluation.flip().winPercent <= WINNING_ADVANTAGE_WIN_PROBABILITY
            }

        if(adjustedAllianceMoves.isEmpty()) return null
        return adjustedAllianceMoves.map(ReviewedMove::centipawnLoss).average()
    }

    private fun getBlunderRate(reviewedMovesForAlliance: List<ReviewedMove>) : Double {
        return reviewedMovesForAlliance.count { it.moveQuality == MoveQuality.BLUNDER }.toDouble() / reviewedMovesForAlliance.size
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
        val willPseudoBlunderIncreaseAverage = blunderMoveNumbers.max() <= (moveQualities.size / 2)
        val timeline = listOf(gameStart) + blunderMoveNumbers + (if(willPseudoBlunderIncreaseAverage) listOf(gameEnd) else emptyList())
        return timeline.zipWithNext { a, b -> b - a }.average()
    }

    private fun getLongestStreakBestOrExcellentPastOpening(reviewedMovesForAlliance: List<ReviewedMove>) : Int {
        val nonOpeningReviewedMovesForAlliance = reviewedMovesForAlliance.filterIndexed {
            index, _ ->  index >= NUMBER_OF_TURNS_TO_EXCLUDE
        }
        val bestOrExcellent = setOf(MoveQuality.BEST, MoveQuality.EXCELLENT)
        return nonOpeningReviewedMovesForAlliance
            .map { if (it.moveQuality in bestOrExcellent) 'S' else ' ' }
            .joinToString("")
            .split(" ")
            .maxOfOrNull { it.length } ?: 0
    }

    companion object{
        const val NUMBER_OF_TURNS_TO_EXCLUDE = 7
        const val WINNING_ADVANTAGE_CENTIPAWNS = 300
        const val WINNING_ADVANTAGE_WIN_PROBABILITY = 0.90
    }
}