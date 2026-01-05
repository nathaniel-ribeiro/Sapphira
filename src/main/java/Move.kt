import java.util.regex.Matcher
import java.util.regex.Pattern

data class Move(val srcSquare: String, val destSquare: String) {
    init {
        //NOTE: this is a necessary but insufficient check for legal moves
        require(SQUARE_PATTERN.matcher(srcSquare).matches())
        require(SQUARE_PATTERN.matcher(destSquare).matches())
        require(!srcSquare.equals(destSquare, ignoreCase = true))
    }

    override fun toString(): String {
        return this.srcSquare + this.destSquare
    }

    companion object {
        val SQUARE_PATTERN: Pattern = Pattern.compile("[a-i]\\d")
    }
}
