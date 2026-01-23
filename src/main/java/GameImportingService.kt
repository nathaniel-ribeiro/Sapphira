import java.util.regex.Pattern

typealias MoveWithThinkTime = Pair<Move, Int>
class GameImportingService {
    /**
     * Convert moves_raw from proprietary user games into native "Move" format.
     * Assumes that each move is stored in long algebraic notation with rows 1-10 instead of 0-9
     * Assumes that each move is followed by three fractions (possibly all 0 for untimed games) denoting time usage
     * Assumes moves are stored in a comma-delimited list.
     */
    fun convertToListOfMoves(movesRaw : String) : List<MoveWithThinkTime>{
        val movesListRaw = movesRaw.removePrefix("[")
                                   .removeSuffix("]")
                                   .split(',')
                                   .map { it.trim().trim('\'') }
                                   .filter { it.isNotEmpty() }

        return movesListRaw.mapIndexed{ i, moveRaw ->
            val matcher = MOVE_WITH_TIME_USAGE_PATTERN.matcher(moveRaw)
            require(matcher.matches()){"$moveRaw did not match pattern $MOVE_WITH_TIME_USAGE_PATTERN"}
            val thinkTime = matcher.group(7).toInt()
            val moveOneIndexed = OneIndexedMove(matcher.group(1), matcher.group(3), if(i.mod(2) == 0) Alliance.RED else Alliance.BLACK)
            val moveZeroIndexed = moveOneIndexed.toZeroIndexedMove()
            MoveWithThinkTime(moveZeroIndexed, thinkTime)
        }.toList()
    }
    companion object{
        // abandon hope all ye who enter here
        // negative numbers possible if there is increment and user takes less time to move than the increment
        private val MOVE_WITH_TIME_USAGE_PATTERN =
            Pattern.compile("([a-i]([1-9]|10))([a-i]([1-9]|10)) (0|\\d+/\\d+) (0|\\d+/\\d+) (-?\\d+)")
    }
}