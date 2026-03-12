data class ReviewedMove(
    val movePlayed : Move,
    val movePlayedEvaluation: Evaluation,
    val bestMoveEvaluation: Evaluation,
    val moveAccuracyCalculator: MoveAccuracyCalculator,
    val moveClassifier: MoveClassifier
){
    val centipawnLoss: Int
        get() = bestMoveEvaluation.centipawns - movePlayedEvaluation.centipawns
    val winPercentDrop : Double
        get() = (bestMoveEvaluation.winPercent - movePlayedEvaluation.winPercent).coerceIn(0.0..100.0)
    val moveAccuracy : Double
        get() = moveAccuracyCalculator.computeMoveAccuracy(winPercentDrop)
    val moveQuality : MoveQuality
        get() = moveClassifier.classify(moveAccuracy)
}