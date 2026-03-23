data class ReviewedMove(
    val movePlayed : Move,
    val movePlayedEvaluation: Evaluation,
    val bestMoveEvaluation: Evaluation,
    val moveAccuracyCalculator: MoveAccuracyCalculator,
    val moveClassifier: MoveClassifier
){
    val centipawnLoss: Int
        get() = (bestMoveEvaluation.centipawns - movePlayedEvaluation.centipawns).coerceAtLeast(MIN_CENTIPAWN_LOSS)
    val winPercentDrop : Double
        get() = (bestMoveEvaluation.winPercent - movePlayedEvaluation.winPercent).coerceIn(MIN_WIN_PERCENT_DROP..MAX_WIN_PERCENT_DROP)
    val moveAccuracy : Double
        get() = moveAccuracyCalculator.computeMoveAccuracy(winPercentDrop)
    val moveQuality : MoveQuality
        get() = moveClassifier.classify(moveAccuracy)

    companion object {
        const val MIN_WIN_PERCENT_DROP = 0.0
        const val MAX_WIN_PERCENT_DROP = 100.0
        const val MIN_CENTIPAWN_LOSS = 0
    }
}