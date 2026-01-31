class GameReviewService(val pikafish: Pikafish) {
    fun review(game: Game, nodesToSearchPerMove : Int): ReviewedGame {
        var curBoard = Board.STARTING_BOARD
        val reviewedMoves = ArrayList<ReviewedMove>()
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
}