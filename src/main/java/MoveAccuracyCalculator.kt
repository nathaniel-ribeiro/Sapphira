import kotlin.math.exp

class MoveAccuracyCalculator {
    fun computeMoveAccuracy(winPercentDrop : Double) : Double {
        // formula from https://lichess.org/page/accuracy
        val rawAccuracy = 103.1668 * exp(-0.04354 * winPercentDrop) - 3.1669
        return rawAccuracy.coerceIn(MIN_MOVE_ACCURACY..MAX_MOVE_ACCURACY)
    }
    companion object {
        const val MIN_MOVE_ACCURACY = 1.0
        const val MAX_MOVE_ACCURACY = 100.0
    }
}