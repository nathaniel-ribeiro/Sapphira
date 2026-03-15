data class Game(
    val uuid : String,
    val redPlayer: Player,
    val blackPlayer: Player,
    val gameTimer: Int,
    val moveTimer: Int,
    val increment: Int,
    val moves: List<Move>,
    val resultRed: GameResult,
    val resultBlack: GameResult,
    val gameResultReason: GameResultReason
)
{
    val isUntimed
        get() = moves.map { it.thinkTime }.all { it == null }
    init{
        require(gameTimer > 0)
        require(moveTimer > 0)
        require(increment >= 0)
        require(moves.all { it.thinkTime != null } || moves.all { it.thinkTime == null })
    }
}