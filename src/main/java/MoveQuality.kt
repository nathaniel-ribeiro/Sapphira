enum class MoveQuality(val dropInWinProbability : ClosedFloatingPointRange<Double>) {
    BEST_OR_EXCELLENT(Double.MIN_VALUE..0.02),
    GOOD(0.02..0.05),
    INACCURACY(0.05..0.10),
    MISTAKE(0.10..0.20),
    BLUNDER(0.20..1.00)
}