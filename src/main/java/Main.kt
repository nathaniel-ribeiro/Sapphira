fun main(){
    val pikafish = Pikafish(ConfigOptions)
    val board = Board("rnbakabnr/9/2c4c1/p1p1p1p1p/9/2P6/P3P1P1P/1C5C1/9/RNBAKABNR w - - 0 1")
    val legalMoves = pikafish.getLegalMoves(board)
    val allEvaluations : List<Double> = legalMoves.map{ move -> -1 * pikafish.evaluate(pikafish.makeMove(board, move)) }
    val moveEvaluations : Map<Move, Double> = legalMoves.zip(allEvaluations).toMap()
    val iprModel = IPRModel(0.078, 0.502)
    val projectedMoveProbabilities = iprModel.getProjectedMoveProbabilities(moveEvaluations)
    val highestProbability = projectedMoveProbabilities.maxBy { it.value }
    println(projectedMoveProbabilities)
}