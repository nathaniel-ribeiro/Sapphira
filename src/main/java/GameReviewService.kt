class GameReviewService(val pikafish: Pikafish) {
    //TODO: this currently makes 3x calls to Pikafish evaluate than strictly necessary.
    // 1x call to get best move
    // 1x call to get evaluation of best move
    // 1x call to get evaluation of move played
    // best move can be extracted from the same call to evaluate, evaluation of best move can be extracted from evaluation of the last board
    fun review(game: Game): ReviewedGame {
        var curBoard = Board.STARTING_BOARD
        val reviewedMoves = buildList {
            game.moves.forEach { move ->
                val bestMove = pikafish.getBestMove(curBoard)
                val bestMoveEvaluation = pikafish.evaluate(curBoard)
                curBoard = pikafish.makeMove(curBoard, move)
                // flip evaluation perspective because it's no longer our turn
                val movePlayedEvaluation = pikafish.evaluate(curBoard).flip()
                add(ReviewedMove(move, bestMove, movePlayedEvaluation, bestMoveEvaluation))
            }
        }
        return ReviewedGame(game, reviewedMoves)
    }
}