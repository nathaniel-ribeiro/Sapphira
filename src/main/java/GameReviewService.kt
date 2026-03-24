class GameReviewService(val pikafish: Pikafish) {
    fun review(game: Game, nodesToSearchPerMove : Int = DEFAULT_NODES_TO_SEARCH_PER_MOVE): ReviewedGame {
        if(nodesToSearchPerMove < MIN_NODES_TO_SEARCH_PER_MOVE) throw IllegalArgumentException()
        var curBoard = Board.STARTING_BOARD
        val reviewedMoves = mutableListOf<ReviewedMove>()
        for((i, move) in game.moves.withIndex()){
            val bestMoveEvaluation =
                if(i == 0) pikafish.evaluate(curBoard, nodesToSearchPerMove)
                else reviewedMoves.map(ReviewedMove::movePlayedEvaluation)[i-1].flip()
            curBoard = pikafish.makeMove(curBoard, move)
            val movePlayedEvaluation = pikafish.evaluate(curBoard, nodesToSearchPerMove).flip()
            reviewedMoves.add(ReviewedMove(move, movePlayedEvaluation, bestMoveEvaluation))
        }
        return ReviewedGame(game, reviewedMoves)
    }
    companion object{
        const val DEFAULT_NODES_TO_SEARCH_PER_MOVE = 3_500_000
        const val MIN_NODES_TO_SEARCH_PER_MOVE = 1
    }
}