import com.google.common.collect.ImmutableList

data class ReviewedGame(
    val game : Game,
    private val _reviewedMoves : List<ReviewedMove>
) {
    val reviewedMoves : List<ReviewedMove>
        get() = ImmutableList.copyOf(this._reviewedMoves)
    init {
        require(this._reviewedMoves.map(ReviewedMove::movePlayed).toList() == this.game.moves )
    }
}