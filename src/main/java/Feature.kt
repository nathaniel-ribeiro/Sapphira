import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.rank.Median
import kotlin.math.abs
import kotlin.math.pow

fun IGame.getThinkTimesFor(alliance: Alliance) : List<Int> {
    require(this.isTimed)
    val thinkTimes = this.moves.map { it.thinkTime }.requireNoNulls()
    return thinkTimes
}

enum class Feature {
    ARITHMETIC_MEAN_GAME_ACCURACY {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesFor(alliance)
            // NOTE: a simple average is used here for legacy/compatibility reasons with Xiangqi.com's source code.
            // a more robust calculation is described here: https://lichess.org/page/accuracy
            val accuracy = reviewedMovesForAlliance.map { it.moveAccuracy }.average()
            return accuracy
        }
    },
    HARMONIC_MEAN_GAME_ACCURACY {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesFor(alliance)
            val denominator = reviewedMovesForAlliance.sumOf { 1.0 / it.moveAccuracy }
            val numerator = reviewedMovesForAlliance.size
            return numerator / denominator
        }
    },
    RATING {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Int {
            val player = if (alliance == Alliance.RED) reviewedGame.redPlayer else reviewedGame.blackPlayer
            return player.rating
        }
    },
    ADJUSTED_CENTIPAWN_LOSS {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesFor(alliance)
            val quietNonOpeningMoves = reviewedMovesForAlliance
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

            if(quietNonOpeningMoves.isEmpty()) return null
            return quietNonOpeningMoves.map { it.centipawnLoss }.average()
        }
    },
    BLUNDER_RATE {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesFor(alliance)
            val blunderRate = reviewedMovesForAlliance.count { it.moveQuality == MoveQuality.BLUNDER } / reviewedMovesForAlliance.size.toDouble()
            return blunderRate
        }
    },
    LONGEST_STREAK_BEST_OR_EXCELLENT_PAST_OPENING {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Int {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesFor(alliance)
            val nonOpeningMoves = reviewedMovesForAlliance.filterIndexed {
                    index, _ ->  index >= NUM_OPENING_FULL_MOVES
            }
            val bestOrExcellent = setOf(MoveQuality.BEST, MoveQuality.EXCELLENT)
            return nonOpeningMoves
                .map { if (it.moveQuality in bestOrExcellent) 'S' else ' ' }
                .joinToString("")
                .split(" ")
                .maxOfOrNull { it.length } ?: 0
        }
    },
    EVALUATION_AFTER_OPENING {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Int? {
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesFor(alliance)
            if(reviewedMovesForAlliance.size <= NUM_OPENING_FULL_MOVES) return null
            return reviewedMovesForAlliance[NUM_OPENING_FULL_MOVES].bestMoveEvaluation.centipawns
        }
    },
    THINK_TIME_MEDIAN {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            if(!reviewedGame.isTimed) return null
            val thinkTimes = reviewedGame.getThinkTimesFor(alliance).map { it.toDouble() }.toDoubleArray()
            val median = Median()
            return median.evaluate(thinkTimes)
        }
    },
    THINK_TIME_IQR {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            if(!reviewedGame.isTimed) return null
            val thinkTimes = reviewedGame.getThinkTimesFor(alliance).map { it.toDouble() }.toDoubleArray()
            val ds = DescriptiveStatistics(thinkTimes)
            val q1 = ds.getPercentile(25.0)
            val q3 = ds.getPercentile(75.0)
            val iqr = q3 - q1
            return iqr
        }
    },
    THINK_TIME_HIGH_OUTLIER_FRACTION {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            if(!reviewedGame.isTimed) return null
            val thinkTimes = reviewedGame.getThinkTimesFor(alliance).map { it.toDouble() }.toDoubleArray()
            val ds = DescriptiveStatistics(thinkTimes)
            val q1 = ds.getPercentile(25.0)
            val q3 = ds.getPercentile(75.0)
            val iqr = q3 - q1
            val tukeyFenceUpperBound = q3 + TUKEY_FENCE_MULTIPLIER * iqr
            val numHighOutliers = thinkTimes.count { it > tukeyFenceUpperBound }
            val highOutlierFraction = numHighOutliers / thinkTimes.size.toDouble()
            return highOutlierFraction
        }
    },
    ACCURACY_OF_LONGEST_THINK {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            if(!reviewedGame.isTimed) return null
            val reviewedMovesForAlliance = reviewedGame.reviewedMovesFor(alliance)
            val thinkTimes = reviewedGame.getThinkTimesFor(alliance)
            val indexOfLongestThink = thinkTimes.indices.maxBy { thinkTimes[it] }
            val accuracyOfLongestThink = reviewedMovesForAlliance[indexOfLongestThink].moveAccuracy
            return accuracyOfLongestThink
        }
    },
    MOVE_NUMBER_OF_LONGEST_THINK {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Int? {
            if(!reviewedGame.isTimed) return null
            val thinkTimes = reviewedGame.getThinkTimesFor(alliance)
            val indexOfLongestThink = thinkTimes.indices.maxBy { thinkTimes[it] }
            val moveNumberOfLongestThink = indexOfLongestThink + 1
            return moveNumberOfLongestThink
        }
    },
    ACTUAL_GAME_SCORE {
        override fun calculate(reviewedGame : ReviewedGame, alliance : Alliance) : Double {
            return if(alliance == Alliance.RED) reviewedGame.resultRed.score
                   else reviewedGame.resultBlack.score
        }
    },
    EXPECTED_GAME_SCORE {
        override fun calculate(reviewedGame: ReviewedGame, alliance: Alliance): Double {
            val me = if(alliance == Alliance.RED) reviewedGame.redPlayer else reviewedGame.blackPlayer
            val opponent = if(alliance == Alliance.RED) reviewedGame.blackPlayer else reviewedGame.redPlayer
            val expectedScore = 1 / (1 + 10.0.pow((opponent.rating - me.rating) / 400))
            return expectedScore
        }
    };
    abstract fun calculate(reviewedGame : ReviewedGame, alliance : Alliance) : Number?

    companion object {
        const val NUM_OPENING_FULL_MOVES = 7
        const val WINNING_ADVANTAGE_CENTIPAWNS = 300
        const val WINNING_ADVANTAGE_WIN_PERCENT = 90.0
        const val TUKEY_FENCE_MULTIPLIER = 1.5
    }
}