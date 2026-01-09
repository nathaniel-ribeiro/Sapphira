import java.util.regex.Pattern

class OneIndexedMove(val srcSquare : String, val destSquare : String) {
    init {
        //NOTE: this is a necessary but insufficient check for legal moves
        require(SQUARE_PATTERN.matcher(srcSquare).matches())
        require(SQUARE_PATTERN.matcher(destSquare).matches())
        require(!srcSquare.equals(destSquare, ignoreCase = true))
    }
    fun toZeroIndexedMove() : Move{
        val zeroIndexedSrcSquare = "${this.srcSquare.substring(0, 1)}${(this.srcSquare.substring(1, this.srcSquare.length).toInt() - 1)}"
        val zeroIndexedDestSquare = "${this.destSquare.substring(0, 1)}${(this.destSquare.substring(1, this.destSquare.length).toInt() - 1)}"
        return Move(zeroIndexedSrcSquare, zeroIndexedDestSquare)
    }
    override fun toString(): String {
        return this.srcSquare + this.destSquare
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneIndexedMove) return false
        return this.srcSquare == other.srcSquare && this.destSquare == other.destSquare
    }

    override fun hashCode(): Int {
        var result = srcSquare.hashCode()
        result = 31 * result + destSquare.hashCode()
        return result
    }

    companion object {
        private val SQUARE_PATTERN : Pattern = Pattern.compile("[a-i]([1-9]|10)")
    }
}