import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver
import kotlin.math.*

class IPRModel(val sensitivity: Double, val consistency: Double) {
    init {
        require(sensitivity > 0) { "Sensitivity must be positive" }
        require(consistency > 0) { "Consistency must be positive" }
    }

    fun getProjectedMoveProbabilities(moveEvaluations: Map<Move, Double>): Map<Move, Double> {
        val moveEvaluationsSorted = moveEvaluations.entries.sortedByDescending { it.value }
        val moves = moveEvaluationsSorted.map { it.key }
        val evaluations = moveEvaluationsSorted.map { it.value }
        val deltas = computeDeltas(evaluations)
        val alphas = computeAlphas(deltas)
        val projectedProbabilities = this.normalize(alphas)
        val projectedMoveProbabilities = moves.zip(projectedProbabilities).toMap()
        return projectedMoveProbabilities
    }

    private fun computeAlphas(deltas: List<Double>): List<Double> {
        return deltas.map{ delta -> exp((delta / this.sensitivity).pow(this.consistency)) }
    }

    private fun computeDeltas(evaluations: List<Double>): List<Double> {
        val bestEvaluation = evaluations.max()
        val antiderivative = UnivariateFunction { z -> sign(z) * ln1p(abs(z)) }
        val upper = antiderivative.value(bestEvaluation)
        val deltas = evaluations.map{ evaluation -> upper - antiderivative.value(evaluation) }
        return deltas
    }

    private fun normalize(alphas: List<Double>): List<Double> {
        // each p_i = p_best ^ {\alpha_i}
        // constraint: \sum p_i = 1 (probability vector)
        val equationToSolve = UnivariateFunction {pBestGuess -> alphas.sumOf { alpha -> pBestGuess.pow(alpha) } - 1.0}
        val solver = BracketingNthOrderBrentSolver()
        val pBest = solver.solve(MAX_ROOTFINDER_ITERATIONS, equationToSolve, 1.0 / alphas.size, MAX_BEST_MOVE_PROJECTED_PROBABILITY)
        val projectedProbabilities = alphas.map{ alpha -> pBest.pow(alpha) }
        return projectedProbabilities
    }

    companion object {
        private const val MAX_BEST_MOVE_PROJECTED_PROBABILITY = 1.0
        private const val MAX_ROOTFINDER_ITERATIONS = 30
    }
}
