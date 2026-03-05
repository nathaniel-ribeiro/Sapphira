import kotlin.math.exp

data class ReviewedMove(
    val movePlayed : Move,
    val movePlayedEvaluation: Evaluation,
    val bestMoveEvaluation: Evaluation,
){
    val centipawnLoss: Int
        get() = bestMoveEvaluation.centipawns - movePlayedEvaluation.centipawns
    val winPercentDrop : Double
        get() = (bestMoveEvaluation.winPercent - movePlayedEvaluation.winPercent).coerceIn(0.0..100.0)
    val moveQuality : MoveQuality
        get() = MoveClassifier.classify(winPercentDrop)
}