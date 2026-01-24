class GameReviewService(val pikafish: Pikafish) {
    fun review(game: Game): ReviewedGame {
        var curBoard = Board.STARTING_BOARD
        val reviewedMoves = ArrayList<ReviewedMove>()
        for((i, move) in game.moves.withIndex()){
            val bestMoveEvaluation =
                if(i == 0) pikafish.evaluate(curBoard)
                else reviewedMoves.map(ReviewedMove::movePlayedEvaluation)[i-1].flip()
            curBoard = pikafish.makeMove(curBoard, move)
            val movePlayedEvaluation = pikafish.evaluate(curBoard).flip()
            reviewedMoves.add(ReviewedMove(move, movePlayedEvaluation, bestMoveEvaluation))
        }
        return ReviewedGame(game, reviewedMoves)
    }
}