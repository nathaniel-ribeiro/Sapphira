import com.google.common.collect.ImmutableList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs

class Pikafish(options: PikafishOptions) {
    private val reader: BufferedReader
    private val writer: PrintWriter
    private val nodesToSearch: Int

    init {
        require(options.numThreads in MIN_THREADS..MAX_THREADS) {
            String.format(
                "Number of threads must be between %d and %d",
                MIN_THREADS,
                MAX_THREADS
            )
        }
        require(options.hashSizeMiB in MIN_HASH_SIZE_MIB..MAX_HASH_SIZE_MIB) {
            String.format(
                "Hash size in MiB must be between %d and %d",
                MIN_HASH_SIZE_MIB,
                MAX_HASH_SIZE_MIB
            )
        }
        require(options.nodesToSearch >= MIN_NODES_TO_SEARCH) {
            String.format(
                "Nodes to search must be at least %d",
                MIN_NODES_TO_SEARCH
            )
        }

        this.nodesToSearch = options.nodesToSearch

        val processBuilder = ProcessBuilder(options.pathToExecutable)
        val process = processBuilder.start()
        this.reader = BufferedReader(InputStreamReader(process.inputStream))
        this.writer = PrintWriter(process.outputStream, true)

        this.send("uci")
        this.send("isready")
        this.send("setoption name Threads value ${options.numThreads}")
        this.send("setoption name Hash value ${options.hashSizeMiB}")
        this.send("setoption name UCI_ShowWDL value true")
    }

    private fun send(command: String) {
        this.writer.println(command)
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

    fun getBestMove(board: Board): Move {
        this.send("position fen ${board.fen}")
        this.send("go nodes ${this.nodesToSearch}")
        val bestMoveLine = this.waitForResponseContaining("bestmove")
        val matcher: Matcher = BEST_MOVE_PATTERN.matcher(bestMoveLine)
        val b = matcher.matches()
        if (!b) throw RuntimeException()
        val srcSquare = matcher.group(1)
        val destSquare = matcher.group(2)
        return Move(srcSquare, destSquare)
    }

    fun makeMove(board: Board, move: Move): Board {
        return this.makeMoves(board, listOf(move))
    }

    fun makeMoves(board: Board, moves: List<Move>): Board {
        if (moves.isEmpty()) return board
        val movesString = moves.joinToString(separator = " ")
        this.send("position fen ${board.fen} moves $movesString")
        // NOTE: this is a Stockfish/Pikafish specific command to display the board/get the final FEN.
        // It is NOT a command guaranteed by the UCI protocol.
        // For compatibility with other UCI engines, this code should be changed.
        this.send("d")
        val fenLine = this.waitForResponseContaining("Fen:")
        val matcher: Matcher = FEN_EXTRACTOR_PATTERN.matcher(fenLine)
        val b = matcher.matches()
        if (!b) throw RuntimeException()
        val fen = matcher.group(1)
        return Board(fen)
    }

    fun evaluate(board: Board): Evaluation {
        this.send("position fen ${board.fen}")
        this.send("go nodes ${this.nodesToSearch}")
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
                    centipawns = if (checkmating) CHECKMATE_EVALUATION_CENTIPAWNS - pliesTilMate else -CHECKMATE_EVALUATION_CENTIPAWNS + pliesTilMate
                }
                else centipawns = matcher.group(2).toInt()
                val winProbability = matcher.group(3).toDouble() / 1000.0
                val drawProbability = matcher.group(4).toDouble() / 1000.0
                val loseProbability = matcher.group(5).toDouble() / 1000.0
                evaluation = Evaluation(centipawns, winProbability, drawProbability, loseProbability)
            }
        }
        return evaluation
    }

    fun getLegalMoves(board: Board): List<Move> {
        this.send("position fen ${board.fen}")
        // NOTE: this is a Stockfish/Pikafish specific command to display the board/get the final FEN.
        // It is NOT a command guaranteed by the UCI protocol.
        // For compatibility with other UCI engines, this code should be changed.
        this.send("go perft 1")
        val moves: MutableList<Move> = ArrayList()
        var line: String
        while ((reader.readLine().also { line = it }) != null) {
            val matcher: Matcher = LEGAL_MOVE_PATTERN.matcher(line)
            if (line.contains("Nodes searched")) break
            if (matcher.matches()) moves.add(Move(matcher.group(1), matcher.group(2)))
        }
        return ImmutableList.copyOf<Move>(moves)
    }

    companion object {
        private const val MIN_THREADS = 1
        private const val MAX_THREADS = 1024
        private const val MIN_HASH_SIZE_MIB = 1
        private const val MAX_HASH_SIZE_MIB = 33554432
        private const val MIN_NODES_TO_SEARCH = 1
        private const val CHECKMATE_EVALUATION_CENTIPAWNS = 10_000

        private val BEST_MOVE_PATTERN: Pattern = Pattern.compile("bestmove ([a-i]\\d)([a-i]\\d)(?:\\s+.*)?")
        private val FEN_EXTRACTOR_PATTERN: Pattern = Pattern.compile("Fen: (.+)")
        private val LEGAL_MOVE_PATTERN: Pattern = Pattern.compile("([a-i]\\d)([a-i]\\d): 1")
        private val EVALUATION_PATTERN: Pattern = Pattern.compile(".*score (mate|cp) (-?\\d+) wdl (\\d+) (\\d+) (\\d+).*")
    }
}
