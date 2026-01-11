enum class MoveQuality(val cpLossRange : IntRange) {
    // Note: the cp loss lower bound for "best" should intuitively be 0 but low depth analysis can miss
    // the true best move and setting the lower bound and setting the lower bound to Int.MIN_VALUE captures the full
    // range of integers
    BEST(Int.MIN_VALUE..<5),
    EXCELLENT(5..<35),
    GOOD(35..<75),
    INACCURACY(75..<105),
    MISTAKE(105..<155),
    BLUNDER(155..<Int.MAX_VALUE)
}