import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class IPRModel {
    private final double sensitivity;
    private final double consistency;

    private static final double MAX_BEST_MOVE_PROJECTED_PROBABILITY = 1.0;
    private static final int MAX_ROOTFINDER_ITERATIONS = 30;

    public IPRModel(final double sensitivity, final double consistency){
        if(sensitivity <= 0) throw new IllegalArgumentException("Sensitivity must be positive");
        if(consistency <= 0) throw new IllegalArgumentException("Consistency must be positive");
        this.sensitivity = sensitivity;
        this.consistency = consistency;
    }
    public Map<Move, Double> getProjectedMoveProbabilities(final Map<Move, Double> moveEvaluations){
        final List<Entry<Move, Double>> moveEvaluationsSorted = moveEvaluations.entrySet()
                                                                         .stream()
                                                                         .sorted(Entry.<Move, Double>comparingByValue().reversed())
                                                                         .toList();

        final List<Move> moves = moveEvaluationsSorted.stream().map(Entry::getKey).toList();
        final List<Double> evaluations = moveEvaluationsSorted.stream().map(Entry::getValue).toList();
        final List<Double> deltas = computeDeltas(evaluations);
        final List<Double> alphas = computeAlphas(deltas);
        final List<Double> projectedProbabilities = this.normalize(alphas);
        final Map<Move, Double> projectedMoveProbabilities = IntStream.range(0, moves.size()).boxed().collect(Collectors.toMap(moves::get, projectedProbabilities::get));
        return ImmutableMap.copyOf(projectedMoveProbabilities);
    }

    private List<Double> computeAlphas(final List<Double> deltas){
        return deltas.stream().map(delta -> Math.exp(Math.pow((delta / this.sensitivity), this.consistency))).toList();
    }

    private List<Double> computeDeltas(final List<Double> evaluations){
        final double bestEvaluation = evaluations.stream().max(Double::compareTo).orElseThrow(RuntimeException::new);
        final UnivariateFunction antiderivative = z -> Math.signum(z) * Math.log1p(Math.abs(z));
        final double upper = antiderivative.value(bestEvaluation);
        final List<Double> deltas = evaluations.stream().map(evaluation -> upper - antiderivative.value(evaluation)).toList();
        return ImmutableList.copyOf(deltas);
    }

    private List<Double> normalize(final List<Double> alphas){
        // each p_i = p_best ^ {\alpha_i}
        // constraint: \sum p_i = 1 (probability vector)
        final UnivariateFunction equationToSolve = pBestGuess -> alphas.stream().mapToDouble(alpha -> Math.pow(pBestGuess, alpha)).sum() - 1.0;
        final BracketingNthOrderBrentSolver solver = new BracketingNthOrderBrentSolver();
        final double pBest = solver.solve(MAX_ROOTFINDER_ITERATIONS, equationToSolve, 1.0 / alphas.size(), MAX_BEST_MOVE_PROJECTED_PROBABILITY);
        final List<Double> projectedProbabilities = alphas.stream().map(alpha -> Math.pow(pBest, alpha)).toList();
        return ImmutableList.copyOf(projectedProbabilities);
    }
}
