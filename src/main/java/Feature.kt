import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.rank.Median
import kotlin.math.abs

fun ReviewedGame.reviewedMovesForAlliance(alliance: Alliance) : List<ReviewedMove> =
    reviewedMoves.filter { it.movePlayed.whoMoved == alliance }

enum class Feature : IFeature {
    ACCURACY {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesForAlliance(alliance)
            // NOTE: a simple average is used here for legacy/compatibility reasons with Xiangqi.com's source code.
            // a more robust calculation is described here: https://lichess.org/page/accuracy
            val accuracy = reviewedMovesForAlliance.map { it.moveAccuracy }.average()
            return accuracy
        }
    },
    RATING {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Int {
            val player = if (alliance == Alliance.RED) reviewedGame.game.redPlayer else reviewedGame.game.blackPlayer
            return player.rating
        }
    },
    ADJUSTED_CENTIPAWN_LOSS {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesForAlliance(alliance)
            val adjustedAllianceMoves = reviewedMovesForAlliance
                .filterIndexed { index, _ ->  index >= NUM_OPENING_FULL_MOVES}
                .filter {
                    abs(it.bestMoveEvaluation.centipawns) <= WINNING_ADVANTAGE_CENTIPAWNS &&
                    abs(it.movePlayedEvaluation.centipawns) <= WINNING_ADVANTAGE_CENTIPAWNS
                }
                .filter {
                    it.bestMoveEvaluation.winPercent <= WINNING_ADVANTAGE_WIN_PERCENT &&
                    it.bestMoveEvaluation.flip().winPercent <= WINNING_ADVANTAGE_WIN_PERCENT &&
                    it.movePlayedEvaluation.winPercent <= WINNING_ADVANTAGE_WIN_PERCENT &&
                    it.movePlayedEvaluation.flip().winPercent <= WINNING_ADVANTAGE_WIN_PERCENT
                }

            if(adjustedAllianceMoves.isEmpty()) return null
            return adjustedAllianceMoves.map(ReviewedMove::centipawnLoss).average()
        }
    },
    BLUNDER_RATE {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesForAlliance(alliance)
            val blunderRate = reviewedMovesForAlliance.count { it.moveQuality == MoveQuality.BLUNDER } / reviewedMovesForAlliance.size.toDouble()
            return blunderRate
        }
    },
    LONGEST_STREAK_BEST_OR_EXCELLENT_PAST_OPENING {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Int {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesForAlliance(alliance)
            val nonOpeningReviewedMovesForAlliance = reviewedMovesForAlliance.filterIndexed {
                    index, _ ->  index >= NUM_OPENING_FULL_MOVES
            }
            val bestOrExcellent = setOf(MoveQuality.BEST, MoveQuality.EXCELLENT)
            return nonOpeningReviewedMovesForAlliance
                .map { if (it.moveQuality in bestOrExcellent) 'S' else ' ' }
                .joinToString("")
                .split(" ")
                .maxOfOrNull { it.length } ?: 0
        }
    },
    EVALUATION_AFTER_OPENING {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Int? {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesForAlliance(alliance)
            if(reviewedMovesForAlliance.size <= NUM_OPENING_FULL_MOVES) return null
            return reviewedMovesForAlliance[NUM_OPENING_FULL_MOVES].bestMoveEvaluation.centipawns
        }
    },
    THINK_TIME_MEDIAN {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            if(reviewedGame.game.isUntimed) return null
            val thinkTimes = reviewedGame.reviewedMovesForAlliance(alliance).map { it.movePlayed.thinkTime }
            require(thinkTimes.all { it != null })
            @Suppress("UNCHECKED_CAST")
            val thinkTimesNonNull = (thinkTimes as List<Int>).map { it.toDouble() }.toDoubleArray()
            val median = Median()
            return median.evaluate(thinkTimesNonNull)
        }
    },
    THINK_TIME_IQR {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            if(reviewedGame.game.isUntimed) return null
            val thinkTimes = reviewedGame.reviewedMovesForAlliance(alliance).map { it.movePlayed.thinkTime }
            @Suppress("UNCHECKED_CAST")
            val thinkTimesNonNull = (thinkTimes as List<Int>).map { it.toDouble() }.toDoubleArray()
            val ds = DescriptiveStatistics(thinkTimesNonNull)
            val q1 = ds.getPercentile(25.0)
            val q3 = ds.getPercentile(75.0)
            val iqr = q3 - q1
            return iqr
        }
    },
    THINK_TIME_OUTLIER_FRACTION {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            if(reviewedGame.game.isUntimed) return null
            val thinkTimes = reviewedGame.reviewedMovesForAlliance(alliance).map { it.movePlayed.thinkTime }
            @Suppress("UNCHECKED_CAST")
            val thinkTimesNonNull = (thinkTimes as List<Int>).map { it.toDouble() }.toDoubleArray()
            val ds = DescriptiveStatistics(thinkTimesNonNull)
            val q1 = ds.getPercentile(25.0)
            val q3 = ds.getPercentile(75.0)
            val iqr = q3 - q1
            val tukeyFenceLowerBound = q1 - TUKEY_FENCE_MULTIPLIER * iqr
            val tukeyFenceUpperBound = q3 + TUKEY_FENCE_MULTIPLIER * iqr
            val numOutliers = thinkTimesNonNull.count { it !in tukeyFenceLowerBound..tukeyFenceUpperBound }
            val outlierFraction = numOutliers / thinkTimesNonNull.size.toDouble()
            return outlierFraction
        }
    },
    ACCURACY_OF_LONGEST_THINK {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            if(reviewedGame.game.isUntimed) return null
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesForAlliance(alliance)
            val moveAccuraciesWithThinkTimes = reviewedMovesForAlliance.map { Pair(it.moveAccuracy, it.movePlayed.thinkTime) }
            @Suppress("UNCHECKED_CAST")
            val moveAccuraciesWithThinkTimesNonNull = moveAccuraciesWithThinkTimes as List<Pair<Double, Int>>
            return moveAccuraciesWithThinkTimesNonNull.maxBy { it.second }.first
        }
    },
    MOVE_NUMBER_OF_LONGEST_THINK {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Int? {
            if(reviewedGame.game.isUntimed) return null
            val thinkTimes = reviewedGame.reviewedMovesForAlliance(alliance).map { it.movePlayed.thinkTime }
            @Suppress("UNCHECKED_CAST")
            val thinkTimesNonNull = (thinkTimes as List<Int>).map { it.toDouble() }.toDoubleArray()
            val indexOfLongestThink = thinkTimesNonNull.indices.maxBy { thinkTimesNonNull[it] }
            val moveNumberOfLongestThink = indexOfLongestThink + 1
            return moveNumberOfLongestThink
        }
    };

    companion object {
        const val NUM_OPENING_FULL_MOVES = 7
        const val WINNING_ADVANTAGE_CENTIPAWNS = 300
        const val WINNING_ADVANTAGE_WIN_PERCENT = 90.0
        const val TUKEY_FENCE_MULTIPLIER = 1.5
    }
}