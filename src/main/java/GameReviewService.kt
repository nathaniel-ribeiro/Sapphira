class GameReviewService(val xiangqiEngine: XiangqiEngine) {
    fun review(game: Game, nodesToSearchPerMove : Int = DEFAULT_NODES_TO_SEARCH_PER_MOVE): ReviewedGame {
        if(nodesToSearchPerMove < 1) throw IllegalArgumentException()
        var curBoard = Board.STARTING_BOARD
        val reviewedMoves = ArrayList<ReviewedMove>()
        for((i, move) in game.moves.withIndex()){
            val bestMoveEvaluation =
                if(i == 0) xiangqiEngine.evaluate(curBoard, nodesToSearchPerMove)
                else reviewedMoves.map(ReviewedMove::movePlayedEvaluation)[i-1].flip()
            curBoard = xiangqiEngine.makeMove(curBoard, move)
            val movePlayedEvaluation = xiangqiEngine.evaluate(curBoard, nodesToSearchPerMove).flip()
            reviewedMoves.add(ReviewedMove(move, movePlayedEvaluation, bestMoveEvaluation))
        }
        return ReviewedGame(game, reviewedMoves)
    }
    companion object{
        const val DEFAULT_NODES_TO_SEARCH_PER_MOVE = 3_500_000
    }
}