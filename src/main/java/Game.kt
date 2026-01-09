import com.google.common.collect.ImmutableList

data class Game(
    val redPlayer: Player,
    val blackPlayer: Player,
    val gameTimer: Int,
    val moveTimer: Int,
    val increment: Int,
    private val _moves: List<Move>,
    val resultRed: GameResult,
    val resultBlack: GameResult,
    val gameResultReason: GameResultReason
)
{
    val moves : List<Move>
        get() = ImmutableList.copyOf(this._moves)
    init{
        require(gameTimer > 0)
        require(moveTimer > 0)
        require(increment >= 0)
    }
}