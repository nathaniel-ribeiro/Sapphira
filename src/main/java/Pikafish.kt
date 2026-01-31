import com.google.common.collect.ImmutableList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs

class Pikafish(pathToExecutable : String, numThreads : Int, hashSizeMiB : Int) {
    private val reader: BufferedReader
    private val writer: PrintWriter

    init {
        require(numThreads in MIN_THREADS..MAX_THREADS) {
            String.format(
                "Number of threads must be between %d and %d",
                MIN_THREADS,
                MAX_THREADS
            )
        }
        require(hashSizeMiB in MIN_HASH_SIZE_MIB..MAX_HASH_SIZE_MIB) {
            String.format(
                "Hash size in MiB must be between %d and %d",
                MIN_HASH_SIZE_MIB,
                MAX_HASH_SIZE_MIB
            )
        }
        val processBuilder = ProcessBuilder(pathToExecutable)
        val process = processBuilder.start()
        reader = BufferedReader(InputStreamReader(process.inputStream))
        writer = PrintWriter(process.outputStream)

        send("uci")
        send("setoption name Threads value $numThreads")
        send("setoption name Hash value $hashSizeMiB")
        send("setoption name UCI_ShowWDL value true")
        readyUp()
        send("ucinewgame")
    }

    private fun send(command: String) {
        writer.println(command)
        writer.flush()
    }

    /**
     * Blocks until given keyword appears in the process's output buffer
     * @param keyword Keyword to wait for
     * @return the line containing the specified keyword
     */
    private fun waitForResponseContaining(keyword: String): String {
        var line: String
        while ((reader.readLine().also { line = it }) != null) {
            if (line.contains(keyword)) {
                return line
            }
        }
        throw RuntimeException("Process terminated before keyword: '${keyword}' was received.")
    }

    fun getBestMove(board: Board, nodesToSearch: Int): Move {
        send("position fen ${board.fen}")
        send("go nodes $nodesToSearch")
        val bestMoveLine = waitForResponseContaining("bestmove")
        val matcher: Matcher = BEST_MOVE_PATTERN.matcher(bestMoveLine)
        val b = matcher.matches()
        if (!b) throw RuntimeException()
        val srcSquare = matcher.group(1)
        val destSquare = matcher.group(2)
        return Move(srcSquare, destSquare, board.whoseTurn)
    }

    fun makeMove(board: Board, move: Move): Board {
        return makeMoves(board, listOf(move))
    }

    fun makeMoves(board: Board, moves: List<Move>): Board {
        if (moves.isEmpty()) return board
        val movesString = moves.joinToString(separator = " ") { it.srcSquare + it.destSquare }
        send("position fen ${board.fen} moves $movesString")
        // NOTE: this is a Stockfish/Pikafish specific command to display the board/get the final FEN.
        // It is NOT a command guaranteed by the UCI protocol.
        // For compatibility with other UCI engines, this code should be changed.
        send("d")
        val fenLine = waitForResponseContaining("Fen:")
        val matcher: Matcher = FEN_EXTRACTOR_PATTERN.matcher(fenLine)
        val b = matcher.matches()
        if (!b) throw RuntimeException()
        val fen = matcher.group(1)
        return Board(fen)
    }

    fun evaluate(board: Board, nodesToSearch : Int): Evaluation {
        send("position fen ${board.fen}")
        send("go nodes $nodesToSearch")
        lateinit var evaluation : Evaluation
        var line: String
        while ((reader.readLine().also { line = it }) != null) {
            val matcher: Matcher = EVALUATION_PATTERN.matcher(line)
            if (line.contains("bestmove")) break
            if (matcher.matches()) {
                val checkmateSoon = matcher.group(1) == "mate"
                val centipawns : Int
                if (checkmateSoon) {
                    val pliesTilMateUnnormalized = matcher.group(2).toInt()
                    val pliesTilMate = abs(pliesTilMateUnnormalized)
                    val checkmating = matcher.group(1) == "mate" && (pliesTilMateUnnormalized > 0)
                    // prefer haste if we are checkmating, prefer stalling if we are getting checkmated
                    centipawns = if (checkmating) Evaluation.WON.centipawns - pliesTilMate else Evaluation.LOST.centipawns + pliesTilMate
                }
                else centipawns = matcher.group(2).toInt()
                try{
                    // the wdl groups will be missing if the game is already over (goes to catch block)
                    val winProbability = matcher.group(4).toDouble() / 1000.0
                    val drawProbability = matcher.group(5).toDouble() / 1000.0
                    val loseProbability = matcher.group(6).toDouble() / 1000.0
                    evaluation = Evaluation(centipawns, winProbability, drawProbability, loseProbability)
                }
                catch (_ : Exception){
                    // extreme case where the game is already over
                    evaluation = if(centipawns > 0) Evaluation.WON else if(centipawns == 0) Evaluation.DREW else Evaluation.LOST
                }
            }
        }
        return evaluation
    }

    fun getLegalMoves(board: Board): List<Move> {
        send("position fen ${board.fen}")
        // NOTE: this is a Stockfish/Pikafish specific command to display the board/get the final FEN.
        // It is NOT a command guaranteed by the UCI protocol.
        // For compatibility with other UCI engines, this code should be changed.
        send("go perft 1")
        val moves = ArrayList<Move>()
        var line: String
        while ((reader.readLine().also { line = it }) != null) {
            val matcher: Matcher = LEGAL_MOVE_PATTERN.matcher(line)
            if (line.contains("Nodes searched")) break
            if (matcher.matches()) moves.add(Move(matcher.group(1), matcher.group(2), board.whoseTurn))
        }
        return ImmutableList.copyOf(moves)
    }

    private fun readyUp(){
        send("isready")
        waitForResponseContaining("readyok")
    }

    fun clear(){
        send("ucinewgame")
        readyUp()
    }

    companion object {
        private const val MIN_THREADS = 1
        private const val MAX_THREADS = 1024
        private const val MIN_HASH_SIZE_MIB = 1
        private const val MAX_HASH_SIZE_MIB = 33554432

        private val BEST_MOVE_PATTERN: Pattern = Pattern.compile("bestmove ([a-i]\\d)([a-i]\\d)(?:\\s+.*)?")
        private val FEN_EXTRACTOR_PATTERN: Pattern = Pattern.compile("Fen: (.+)")
        private val LEGAL_MOVE_PATTERN: Pattern = Pattern.compile("([a-i]\\d)([a-i]\\d): 1")
        // if one side is already checkmated/stalemated, the WDL will not show, even if the show wdl option has been sent
        private val EVALUATION_PATTERN: Pattern = Pattern.compile(".*score (mate|cp) (-?\\d+)( wdl (\\d+) (\\d+) (\\d+))?.*")
    }
}
