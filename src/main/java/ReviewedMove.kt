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
    // formula copied from https://lichess.org/page/accuracy
    val accuracyPercent : Double
        get() = if(movePlayedEvaluation >= bestMoveEvaluation) 100.0
                else (103.1668 * exp(-0.04354 * (bestMoveEvaluation.winPercent - movePlayedEvaluation.winPercent)) - 3.1669).coerceIn(0.0..100.0)
    val moveQuality : MoveQuality
        get() = MoveClassifier.classify(winPercentDrop)
}