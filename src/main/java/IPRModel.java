import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class IPRModel {
    private final IPRParameters parameters;
    public IPRModel(final IPRParameters parameters){
        this.parameters = parameters;
    }
    public Map<Move, Double> getMoveProbabilities(final Map<Move, Double> moveEvaluations){
        final List<Entry<Move, Double>> moveEvaluationsSorted = moveEvaluations.entrySet()
                                                                         .stream()
                                                                         .sorted(Entry.<Move, Double>comparingByValue().reversed())
                                                                         .toList();

        final List<Move> moves = moveEvaluationsSorted.stream().map(Entry::getKey).toList();
        final List<Double> evaluations = moveEvaluationsSorted.stream().map(Entry::getValue).toList();
        final double v_best = evaluations.get(0);
        final List<Double> deltas = evaluations.stream().map(v_i -> {
            final UnivariateFunction antiderivative = z -> (z * Math.log1p(Math.abs(z))) / Math.abs(z);
            final double upper = antiderivative.value(v_best);
            final double lower = antiderivative.value(v_i);
            return upper - lower;
        }).toList();
        final List<Double> alphas = deltas.stream()
                .map(delta_i -> Math.exp(Math.pow((delta_i / parameters.sensitivity()), parameters.consistency())))
                .toList();
        final List<Double> probabilities = this.normalize(alphas);
        final Map<Move, Double> moveProbabilities = IntStream.range(0, moves.size()).boxed().collect(Collectors.toMap(moves::get, probabilities::get));
        return ImmutableMap.copyOf(moveProbabilities);
    }

    private List<Double> normalize(final List<Double> alphas){
        // each p_i = p_best ^ {\alpha_i}
        // constraint: \sum p_i = 1 (probability vector)
        final UnivariateFunction function = p_best -> {
            double sum = 0;
            for(final double alpha : alphas){
                sum += Math.pow(p_best, alpha);
            }
            sum = sum - 1;
            return sum;
        };
        final BisectionSolver solver = new BisectionSolver();
        final double p_best = solver.solve(1000, function, 0.0, 1.0);
        final List<Double> probabilities = alphas.stream().map(alpha -> Math.pow(p_best, alpha)).toList();
        return ImmutableList.copyOf(probabilities);
    }
}
