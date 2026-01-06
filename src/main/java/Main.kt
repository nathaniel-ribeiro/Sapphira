fun main(){
    val pikafish = Pikafish(ConfigOptions)
    val board = Board.STARTING_BOARD
    val legalMoves = pikafish.getLegalMoves(board)
    val allEvaluations : List<Double> = legalMoves.map{ move -> -1 * pikafish.evaluate(pikafish.makeMove(board, move)) }
    val moveEvaluations : Map<Move, Double> = legalMoves.zip(allEvaluations).toMap()
    val iprModel = IPRModel(0.078, 0.502)
    val projectedMoveProbabilities = iprModel.getProjectedMoveProbabilities(moveEvaluations)
    val highestProbability = projectedMoveProbabilities.maxBy { it.value }
    println(projectedMoveProbabilities)
}