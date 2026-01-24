data class ReviewedMove(
    val movePlayed : Move,
    val bestMove : Move,
    val movePlayedEvaluation: Evaluation,
    val bestMoveEvaluation: Evaluation,
){
    val centipawnLoss: Int
        get() = bestMoveEvaluation.centipawns - movePlayedEvaluation.centipawns
    val winProbabilityLoss : Double
        get() = bestMoveEvaluation.winProbability - movePlayedEvaluation.winProbability
    val moveQuality : MoveQuality
        get() = MoveClassifier.classify(winProbabilityLoss)
}