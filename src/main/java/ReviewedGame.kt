data class ReviewedGame(
    private val game : Game,
    val reviewedMoves : List<ReviewedMove>
) : IGame by game {
    init {
        require(this.reviewedMoves.map{ it.movePlayed }.toList() == this.game.moves )
    }
}

fun ReviewedGame.reviewedMovesFor(alliance: Alliance) : List<ReviewedMove> {
    return reviewedMoves.filter { it.whoMoved == alliance }
}