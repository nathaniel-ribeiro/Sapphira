class GameReviewService(val pikafish: Pikafish) {
    fun review(game: Game) : ReviewedGame {
        val (_, reviewedMoves) = game.moves.fold(Board.STARTING_BOARD to emptyList<ReviewedMove>()) { (board, reviewed), move ->
            val bestMove = pikafish.getBestMove(board)
            // not our turn after we play the move, so have to flip the evaluation
            val bestMoveEvaluation = pikafish.evaluate(pikafish.makeMove(board, bestMove)).flip()
            val nextBoard = pikafish.makeMove(board, move)
            // not our turn after we play the move, so have to flip the evaluation
            val movePlayedEvaluation = pikafish.evaluate(nextBoard).flip()
            val reviewedMove = ReviewedMove(move, bestMove, movePlayedEvaluation, bestMoveEvaluation)
            nextBoard to (reviewed + reviewedMove)
        }
        return ReviewedGame(game, reviewedMoves)
    }
}