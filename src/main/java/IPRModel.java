import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
import org.apache.commons.math3.exception.ConvergenceException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class IPRModel {
    private final double sensitivity;
    private final double consistency;

    private static final double MIN_BEST_MOVE_PROJECTED_PROBABILITY = 0.0;
    private static final double MAX_BEST_MOVE_PROJECTED_PROBABILITY = 1.0;
    private static final int MAX_ROOTFINDER_ITERATIONS = 100;

    public IPRModel(final double sensitivity, final double consistency){
        if(sensitivity <= 0) throw new IllegalArgumentException("Sensitivity must be positive");
        if(consistency <= 0) throw new IllegalArgumentException("Consistency must be positive");
        this.sensitivity = sensitivity;
        this.consistency = consistency;
    }
    public Map<Move, Double> getProjectedMoveProbabilities(final Map<Move, Integer> moveEvaluations){
        final List<Entry<Move, Integer>> moveEvaluationsSorted = moveEvaluations.entrySet()
                                                                         .stream()
                                                                         .sorted(Entry.<Move, Integer>comparingByValue().reversed())
                                                                         .toList();

        final List<Move> moves = moveEvaluationsSorted.stream().map(Entry::getKey).toList();
        final List<Integer> evaluations = moveEvaluationsSorted.stream().map(Entry::getValue).toList();
        final List<Double> deltas = computeDeltas(evaluations);
        final List<Double> alphas = deltas.stream()
                .map(delta -> Math.exp(-Math.pow((delta / this.sensitivity), this.consistency)))
                .toList();
        final List<Double> projectedProbabilities = this.normalize(alphas);
        final Map<Move, Double> projectedMoveProbabilities = IntStream.range(0, moves.size()).boxed().collect(Collectors.toMap(moves::get, projectedProbabilities::get));
        return ImmutableMap.copyOf(projectedMoveProbabilities);
    }

    private List<Double> computeDeltas(final List<Integer> evaluations){
        final int bestEvaluation = evaluations.stream().max(Integer::compareTo).orElseThrow(RuntimeException::new);
        final List<Double> deltas = evaluations.stream().map(evaluation -> {
            final UnivariateFunction antiderivative = z -> (z * Math.log1p(Math.abs(z))) / Math.abs(z);
            final double upper = antiderivative.value(bestEvaluation);
            final double lower = antiderivative.value(evaluation);
            return upper - lower;
        }).toList();
        return ImmutableList.copyOf(deltas);
    }

    private List<Double> normalize(final List<Double> alphas){
        // each p_i = p_best ^ {\alpha_i}
        // constraint: \sum p_i = 1 (probability vector)
        final UnivariateFunction function = guessForPBest -> alphas.stream().mapToDouble(alpha -> Math.pow(guessForPBest, alpha)).sum() - 1.0;
        final BracketingNthOrderBrentSolver solver = new BracketingNthOrderBrentSolver();
        final double pBest = solver.solve(MAX_ROOTFINDER_ITERATIONS, function, MIN_BEST_MOVE_PROJECTED_PROBABILITY, MAX_BEST_MOVE_PROJECTED_PROBABILITY);
        final List<Double> projectedProbabilities = alphas.stream().map(alpha -> Math.pow(pBest, alpha)).toList();
        final double totalProbabilityMass = projectedProbabilities.stream().reduce(Double::sum).orElseThrow(RuntimeException::new);
        if(Math.abs(totalProbabilityMass - 1) > 1e-3)
            throw new ConvergenceException();
        return ImmutableList.copyOf(projectedProbabilities);
    }
}
