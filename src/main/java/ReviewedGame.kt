data class ReviewedGame(
    private val game : IGame,
    val reviewedMoves : List<ReviewedMove>
) : IGame by game {
    init {
        require(this.reviewedMoves.map{ it.movePlayed }.toList() == this.game.moves )
    }
}