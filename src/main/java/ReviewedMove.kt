import kotlin.math.exp

data class ReviewedMove(
    val movePlayed : Move,
    val movePlayedEvaluation: Evaluation,
    val bestMoveEvaluation: Evaluation
){
    val centipawnLoss: Int
        get() = (bestMoveEvaluation.centipawns - movePlayedEvaluation.centipawns).coerceAtLeast(MIN_CENTIPAWN_LOSS)
    val winPercentDrop : Double
        get() = (bestMoveEvaluation.winPercent - movePlayedEvaluation.winPercent).coerceIn(MIN_WIN_PERCENT_DROP..MAX_WIN_PERCENT_DROP)
    val moveAccuracy : Double
        get() {
            val rawAccuracy = 103.1668 * exp(-0.04354 * winPercentDrop) - 3.1669
            return rawAccuracy.coerceIn(MIN_MOVE_ACCURACY..MAX_MOVE_ACCURACY)
        }
    val moveQuality : MoveQuality
        get() {
            return when {
                moveAccuracy >= 99.9 -> MoveQuality.BEST
                moveAccuracy > 91 -> MoveQuality.EXCELLENT
                moveAccuracy > 80 -> MoveQuality.GOOD
                moveAccuracy > 63 -> MoveQuality.INACCURACY
                moveAccuracy > 40 -> MoveQuality.MISTAKE
                else -> MoveQuality.BLUNDER
            }
        }

    companion object {
        const val MIN_WIN_PERCENT_DROP = 0.0
        const val MAX_WIN_PERCENT_DROP = 100.0
        const val MIN_CENTIPAWN_LOSS = 0
        const val MIN_MOVE_ACCURACY = 30.0
        const val MAX_MOVE_ACCURACY = 100.0
    }
}