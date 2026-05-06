enum class GameResult(val score : Double) {
    WON(1.0) {
        override fun flip(): GameResult {
            return LOST
        }
    },
    DRAW(0.5) {
        override fun flip(): GameResult {
            return DRAW
        }
    },
    LOST(0.0) {
        override fun flip(): GameResult {
            return WON
        }
    };
    abstract fun flip() : GameResult
}
