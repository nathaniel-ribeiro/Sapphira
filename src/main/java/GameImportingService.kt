import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import java.io.File
import java.util.regex.Pattern

typealias MoveWithThinkTime = Pair<Move, Int>
class GameImportingService {
    fun readData(csvFile : File) : DataFrame<*> {
        val df = DataFrame.readCsv(csvFile)
        for(row in df){
            val redPlayer = Player(row["red_username"] as String, row["red_is_guest"] as Boolean, row["red_is_banned"] as Boolean, row["red_rating"] as Int)
            val blackPlayer = Player(row["black_username"] as String, row["black_is_guest"] as Boolean, row["black_is_banned"] as Boolean, row["black_rating"] as Int)
            val usernameSimilarity = JaroWinklerSimilarity().apply(redPlayer.username, blackPlayer.username)
            val movesWithThinkTime = GameImportingService().convertToListOfMoves(row["moves_raw"] as String)
            val moves = movesWithThinkTime.map { it.first }
            val thinkTimes = movesWithThinkTime.map { it.second }
            val redThinkTimes = thinkTimes.filterIndexed { index, _ ->  index.mod(2) == 0}
            val blackThinkTimes = thinkTimes.filterIndexed { index, _ ->  index.mod(2) == 1}

            val redThinkTimeMean = redThinkTimes.average()
            val redThinkTimeStd = StandardDeviation().evaluate(redThinkTimes.toIntArray().map { it.toDouble() }.toDoubleArray())

            val blackThinkTimeMean = blackThinkTimes.average()
            val blackThinkTimeStd = StandardDeviation().evaluate(blackThinkTimes.toIntArray().map { it.toDouble() }.toDoubleArray())

            val gameTimer = row["game_timer"] as Int
            val moveTimer = row["move_timer"] as Int
            val increment = row["increment"] as Int
            val endReason = GameResultReason.valueOf((row["end_reason"] as String).uppercase().replace(" ", "_"))
            val resultRed = GameResult.valueOf((row["result_red"] as String).uppercase())
            val resultBlack = GameResult.valueOf((row["result_black"] as String).uppercase())
            val game = Game(redPlayer, blackPlayer, gameTimer, moveTimer, increment, moves, resultBlack, resultRed, endReason)
        }
        TODO()
    }
    /**
     * Convert moves_raw from proprietary user games into native "Move" format.
     * Assumes that each move is stored in long algebraic notation with rows 1-10 instead of 0-9
     * Assumes that each move is followed by three fractions (possibly all 0 for untimed games) denoting time usage
     * Assumes moves are stored in a comma-delimited list.
     */
    private fun convertToListOfMoves(movesRaw : String) : List<MoveWithThinkTime>{
        val movesListRaw = movesRaw.removePrefix("[")
                                   .removeSuffix("]")
                                   .split(',')
                                   .map { it.trim().trim('\'') }
                                   .filter { it.isNotEmpty() }

        return movesListRaw.map{ moveRaw ->
            val matcher = MOVE_WITH_TIME_USAGE_PATTERN.matcher(moveRaw)
            require(matcher.matches()){"$moveRaw did not match pattern $MOVE_WITH_TIME_USAGE_PATTERN"}
            val thinkTime = matcher.group(7).toInt()
            val moveOneIndexed = OneIndexedMove(matcher.group(1), matcher.group(3))
            val moveZeroIndexed = moveOneIndexed.toZeroIndexedMove()
            MoveWithThinkTime(moveZeroIndexed, thinkTime)
        }.toList()
    }
    companion object{
        // abandon hope all ye who enter here
        private val MOVE_WITH_TIME_USAGE_PATTERN =
            Pattern.compile("([a-i]([1-9]|10))([a-i]([1-9]|10)) (0|\\d+/-?\\d+) (0|\\d+/-?\\d+) (\\d+)")
    }
}