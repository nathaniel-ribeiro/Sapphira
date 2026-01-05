import java.util.regex.Matcher
import java.util.regex.Pattern

data class Board(val fen: String) {
    val whoseTurn: Alliance
    val pliesSinceACapture: Int
    val fullMoveNumber: Int

    init {
        // NOTE: this check for valid FEN is not rigorous.
        // The following regex will match things that look like FEN but are in fact nonsensical positions
        val matcher: Matcher = FEN_VALIDATOR_PATTERN.matcher(fen)
        require(matcher.matches()) { "Invalid FEN" }
        this.whoseTurn = if (matcher.group(2) == "w") Alliance.RED else Alliance.BLACK
        this.pliesSinceACapture = matcher.group(3).toInt()
        this.fullMoveNumber = matcher.group(4).toInt()
    }

    override fun toString(): String {
        return this.fen
    }

    companion object {
        private val FEN_VALIDATOR_PATTERN =
            Pattern.compile("([kabrcnpKABRCNP0-9]+/?){10} ([wb]) - - (\\d+) (\\d+)")
        private const val STARTING_FEN = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1"
        val STARTING_BOARD = Board(STARTING_FEN)
    }
}
