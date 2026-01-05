data class Game(
    val uuid: String,
    val redPlayer: Player,
    val blackPlayer: Player,
    val gameTimer: Int,
    val moveTimer: Int,
    val increment: Int,
    val gameStates: List<Board>,
    val moves: List<Move>,
    val resultRed: GameResult,
    val resultBlack: GameResult,
    val gameResultReason: GameResultReason
)
