class GameReviewService(val pikafish: Pikafish) {
    //TODO: this currently makes 3x calls to Pikafish evaluate than strictly necessary.
    // 1x call to get best move
    // 1x call to get evaluation of best move
    // 1x call to get evaluation of move played
    // best move can be extracted from the same call to evaluate, evaluation of best move can be extracted from evaluation of the last board
    fun review(game: Game): ReviewedGame {
        var curBoard = Board.STARTING_BOARD
        val reviewedMoves = ArrayList<ReviewedMove>()
        for((i, move) in game.moves.withIndex()){
            val bestMove = pikafish.getBestMove(curBoard)
            val bestMoveEvaluation = if(i >= 1)
                reviewedMoves.map(ReviewedMove::movePlayedEvaluation)[i-1].flip() else pikafish.evaluate(curBoard)
            curBoard = pikafish.makeMove(curBoard, move)
            val movePlayedEvaluation = pikafish.evaluate(curBoard).flip()
            reviewedMoves.add(ReviewedMove(move, bestMove, movePlayedEvaluation, bestMoveEvaluation))
        }
        return ReviewedGame(game, reviewedMoves)
    }
}