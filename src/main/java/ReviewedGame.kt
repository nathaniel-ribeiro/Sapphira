data class ReviewedGame(
    val game : Game,
    val reviewedMoves : List<ReviewedMove>
) : IGame by game {
    init {
        require(this.reviewedMoves.map(ReviewedMove::movePlayed).toList() == this.game.moves )
    }
}