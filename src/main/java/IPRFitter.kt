import kotlin.math.ln

class IPRFitter(val learningRate: Double = DEFAULT_LEARNING_RATE) {
    fun partialFit(moveEvaluations: Map<Move, Double>, movePlayed: Move): IPRModel {
        return this.partialFit(
            IPRModel(SENSITIVITY_INITIAL_GUESS, CONSISTENCY_INITIAL_GUESS),
            moveEvaluations,
            movePlayed
        )
    }

    fun partialFit(model: IPRModel, moveEvaluations: Map<Move, Double>, movePlayed: Move): IPRModel {
        require(moveEvaluations.containsKey(movePlayed))
        val projectedMoveProbabilities: Map<Move, Double> = model.getProjectedMoveProbabilities(moveEvaluations)
        val logLikelihood = ln(projectedMoveProbabilities[movePlayed] ?: throw RuntimeException())
        TODO("compute gradient of likelikhood")
    }

    companion object {
        // rough values taken from https://cse.buffalo.edu/~regan/papers/pdf/Reg12IPRs.pdf Table 2, row for 1600 ELO
        private const val SENSITIVITY_INITIAL_GUESS = 0.165
        private const val CONSISTENCY_INITIAL_GUESS = 0.431
        private const val DEFAULT_LEARNING_RATE = 0.01
    }
}
