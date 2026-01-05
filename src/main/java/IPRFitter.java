import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IPRFitter {
    // rough values taken from https://cse.buffalo.edu/~regan/papers/pdf/Reg12IPRs.pdf Table 2, row for 1600 ELO
    private static final double SENSITIVITY_INITIAL_GUESS = 0.165;
    private static final double CONSISTENCY_INITIAL_GUESS = 0.431;
    private static final double DEFAULT_LEARNING_RATE = 0.01;

    private final double learningRate;

    public IPRFitter(){
        this(DEFAULT_LEARNING_RATE);
    }

    public IPRFitter(final double learningRate){
        this.learningRate = learningRate;
    }

    public IPRModel partialFit(Map<Move, Double> moveEvaluations, Move movePlayed){
        return this.partialFit(new IPRModel(SENSITIVITY_INITIAL_GUESS, CONSISTENCY_INITIAL_GUESS), moveEvaluations, movePlayed);
    }

    public IPRModel partialFit(IPRModel model, Map<Move, Double> moveEvaluations, Move movePlayed){
        if(!moveEvaluations.containsKey(movePlayed)) throw new IllegalArgumentException();
        final Map<Move, Double> projectedMoveProbabilities = model.getProjectedMoveProbabilities(moveEvaluations);
        final double logLikelihood = Math.log(projectedMoveProbabilities.get(movePlayed));
        // TODO: grad1 = gradient of logLikelihood w.r.t sensitivity
        // TODO: grad2 = gradient of logLikelihood w.r.t consistency
        // TODO: sensitivity = sensitivity + learningRate * grad1
        // TODO: consistency = consistency + learningRate * grad2
        // TODO: return new model
        return new IPRModel(SENSITIVITY_INITIAL_GUESS, CONSISTENCY_INITIAL_GUESS);
    }
}
