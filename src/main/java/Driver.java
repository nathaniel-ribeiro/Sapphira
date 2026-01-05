import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Driver {
    public static void main(final String[] args){
        final Pikafish pikafish = new Pikafish(ConfigOptions.INSTANCE);
        final Board board = Board.STARTING_BOARD;
        final List<Move> allLegalMoves = pikafish.getLegalMoves(board);
        final List<Double> evaluations = allLegalMoves.stream()
                                        .map(move -> pikafish.makeMove(board, move))
                                        .map(pikafish::evaluate)
                                        // flip evaluation to be from our perspective
                                        .map(d -> -1 * d)
                                        .toList();
        final Map<Move, Double> moveEvaluations = IntStream.range(0, allLegalMoves.size()).boxed().collect(Collectors.toMap(allLegalMoves::get, evaluations::get));
        final IPRModel model = new IPRModel(0.139, 0.454);
        final Map<Move, Double> map = model.getProjectedMoveProbabilities(moveEvaluations);
    }
}
