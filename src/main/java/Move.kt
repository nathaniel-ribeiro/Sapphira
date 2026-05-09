import java.util.regex.Pattern

data class Move(override val srcSquare: String,
                override val destSquare: String,
                override val whoMoved : Alliance,
                override val thinkTime : Int? = null) : IMove {
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
        private val SQUARE_PATTERN: Pattern = Pattern.compile("[a-i]\\d")
    }
}
