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
                                        .map(d -> -1 * d)
                                        // flip evaluation to be from our perspective
                                        .toList();
        final Map<Move, Double> moveEvaluations = IntStream.range(0, allLegalMoves.size()).boxed().collect(Collectors.toMap(allLegalMoves::get, evaluations::get));
        final IPRParameters parameters = new IPRParameters(0.139, 0.454);
        final IPRModel model = new IPRModel(parameters);
        final Map<Move, Double> map = model.getProjectedMoveProbabilities(moveEvaluations);

        System.out.println("Central cannon: " + map.get(new Move("h2", "e2")));
        System.out.println("Soldier opening: " + map.get(new Move("c3", "c4")));
        System.out.println("Flying elephant: " + map.get(new Move("g0", "e2")));
        System.out.println("Palcorner cannon: " + map.get(new Move("h2", "f2")));
    }
}
