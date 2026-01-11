enum class MoveQuality(val dropInWinProbability : ClosedFloatingPointRange<Double>) {
    // Note: the cp loss lower bound for "best" should intuitively be 0 but low depth analysis can miss
    // the true best move and setting the lower bound and setting the lower bound to Int.MIN_VALUE captures the full
    // range of integers
    BEST_OR_EXCELLENT(0.00..0.02),
    GOOD(0.02..0.05),
    INACCURACY(0.05..0.10),
    MISTAKE(0.10..0.20),
    BLUNDER(0.20..1.00)
}