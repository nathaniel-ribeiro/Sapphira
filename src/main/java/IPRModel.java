import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
import org.apache.commons.math3.analysis.solvers.BrentSolver;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class IPRModel {
    private final IPRParameters parameters;

    private static final double MIN_BEST_MOVE_PROJECTED_PROBABILITY = 0.0;
    private static final double MAX_BEST_MOVE_PROJECTED_PROBABILITY = 1.0;
    private static final int MAX_ROOTFINDER_ITERATIONS = 100;

    public IPRModel(final IPRParameters parameters){
        this.parameters = parameters;
    }
    public Map<Move, Double> getProjectedMoveProbabilities(final Map<Move, Double> moveEvaluations){
        final List<Entry<Move, Double>> moveEvaluationsSorted = moveEvaluations.entrySet()
                                                                         .stream()
                                                                         .sorted(Entry.<Move, Double>comparingByValue().reversed())
                                                                         .toList();

        final List<Move> moves = moveEvaluationsSorted.stream().map(Entry::getKey).toList();
        final List<Double> evaluations = moveEvaluationsSorted.stream().map(Entry::getValue).toList();
        final double bestEvaluation = evaluations.get(0);
        final List<Double> deltas = evaluations.stream().map(evaluation -> {
            final UnivariateFunction antiderivative = z -> (z * Math.log1p(Math.abs(z))) / Math.abs(z);
            final double upper = antiderivative.value(bestEvaluation);
            final double lower = antiderivative.value(evaluation);
            return upper - lower;
        }).toList();
        final List<Double> alphas = deltas.stream()
                .map(delta -> Math.exp(-Math.pow((delta / parameters.sensitivity()), parameters.consistency())))
                .toList();
        final List<Double> projectedProbabilities = this.normalize(alphas);
        final Map<Move, Double> projectedMoveProbabilities = IntStream.range(0, moves.size()).boxed().collect(Collectors.toMap(moves::get, projectedProbabilities::get));
        return ImmutableMap.copyOf(projectedMoveProbabilities);
    }

    private List<Double> normalize(final List<Double> alphas){
        // each p_i = p_best ^ {\alpha_i}
        // constraint: \sum p_i = 1 (probability vector)
        final UnivariateFunction function = guessForPBest -> alphas.stream().mapToDouble(alpha -> Math.pow(guessForPBest, alpha)).sum() - 1.0;
        final BracketingNthOrderBrentSolver solver = new BracketingNthOrderBrentSolver();
        final double pBest = solver.solve(MAX_ROOTFINDER_ITERATIONS,
                                          function,
                                          MIN_BEST_MOVE_PROJECTED_PROBABILITY,
                                          MAX_BEST_MOVE_PROJECTED_PROBABILITY);

        final List<Double> projectedProbabilities = alphas.stream().map(alpha -> Math.pow(pBest, alpha)).toList();
        return ImmutableList.copyOf(projectedProbabilities);
    }
}
