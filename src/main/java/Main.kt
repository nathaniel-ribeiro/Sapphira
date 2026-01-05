import java.util.stream.Collectors
import java.util.stream.IntStream

fun main(){
    val pikafish = Pikafish(ConfigOptions.INSTANCE)
    val board = Board("rnbakabnr/9/2c4c1/p3p1p1p/2p6/9/P3P1P1P/1C2C4/9/RNBAKABNR b - - 0 1")
    val legalMoves = pikafish.getLegalMoves(board)
    val allEvaluations : List<Double> = legalMoves.map{move -> pikafish.makeMove(board, move)}
                                                    .map{board -> -1 * pikafish.evaluate(board)}
                                                    .toList()
    val moveEvaluations : Map<Move, Double> = IntStream.range(0, legalMoves.size).boxed().collect(Collectors.toMap(legalMoves::get, allEvaluations::get))
    val iprModel = IPRModel(0.078, 0.502)
    val projectedMoveProbabilities = iprModel.getProjectedMoveProbabilities(moveEvaluations)
    val highestProbability = projectedMoveProbabilities.values.stream().max(Double::compareTo).orElseThrow()
    println(highestProbability)
}