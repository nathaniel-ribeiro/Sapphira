data class Game(
    override val uuid : String,
    override val redPlayer: Player,
    override val blackPlayer: Player,
    override val gameTimer: Int,
    override val moveTimer: Int,
    override val increment: Int,
    override val moves: List<Move>,
    override val resultRed: GameResult,
    override val resultBlack: GameResult,
    override val gameResultReason: GameResultReason
) : IGame
{
    init{
        require(gameTimer > 0)
        require(moveTimer > 0)
        require(increment >= 0)
        // TODO: create separate TimedMove and/or TimedGame classes, untimed moves and games have no use for this field
        require(moves.all { it.thinkTime != null } || moves.all { it.thinkTime == null })
    }
}