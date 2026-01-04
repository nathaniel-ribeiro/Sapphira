import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Driver {
    public static void main(final String[] args){
        final Pikafish pikafish = new Pikafish(ConfigOptions.INSTANCE);
        final Board board = pikafish.makeMoves(Board.STARTING_BOARD,
                List.of(
                        new Move("h2", "e2"),
                        new Move("h9", "g7")
                )
        );
        final List<Move> allLegalMoves = pikafish.getLegalMoves(board);
        final List<Double> evaluations = allLegalMoves.stream()
                                        .map(move -> pikafish.makeMove(board, move))
                                        .map(pikafish::evaluate)
                                        .map(d -> -1 * d)
                                        // flip evaluation to be from our perspective
                                        .toList();
        final Map<Move, Double> moveEvaluations = IntStream.range(0, allLegalMoves.size()).boxed().collect(Collectors.toMap(allLegalMoves::get, evaluations::get));
        final IPRParameters parameters = new IPRParameters(0.139, 0.454);
        final IPRModel model = new IPRModel(parameters);
        System.out.println(model.getMoveProbabilities(moveEvaluations));
    }
}
