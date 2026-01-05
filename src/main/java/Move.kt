import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

private val SQUARE_PATTERN: Pattern = Pattern.compile("[a-i]\\d")
class Move(srcSquare: String, destSquare: String) {
    val srcSquare: String
    val destSquare: String

    init {
        //NOTE: this is a necessary but insufficient check for legal moves
        var matcher: Matcher = SQUARE_PATTERN.matcher(srcSquare)
        require(matcher.matches()) { "Source square must be a-i followed by a number (e.g. i0)." }
        matcher = SQUARE_PATTERN.matcher(destSquare)
        require(matcher.matches()) { "Destination square must be a-i followed by a number (e.g. i0)." }
        require(!srcSquare.equals(destSquare, ignoreCase = true)) { "Source square cannot be the same as destination square." }
        this.srcSquare = srcSquare.lowercase()
        this.destSquare = destSquare.lowercase()
    }

    override fun toString(): String {
        return this.srcSquare + this.destSquare
    }
}
