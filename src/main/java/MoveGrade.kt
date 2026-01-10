enum class MoveGrade(val minCPLoss : Int, val maxCPLoss : Int) {
    BEST(0, 1),
    EXCELLENT(1, 21),
    GOOD(21, 51),
    INACCURACY(51, 101),
    MISTAKE(101, 151),
    BLUNDER(151, Int.MAX_VALUE)
}