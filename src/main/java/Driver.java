import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Driver {
    public static void main(final String[] args){
        final Pikafish pikafish = new Pikafish(ConfigOptions.INSTANCE);
        final Board board = new Board("rnbakabnr/9/2c4c1/p3p1p1p/2p6/9/P3P1P1P/1C2C4/9/RNBAKABNR b - - 0 1");
        final List<Move> allLegalMoves = pikafish.getLegalMoves(board);
        final List<Double> evaluations = allLegalMoves.stream()
                                        .map(move -> pikafish.makeMove(board, move))
                                        .map(pikafish::evaluate)
                                        // flip evaluation to be from our perspective
                                        .map(d -> -1 * d)
                                        .toList();
        final Map<Move, Double> moveEvaluations = IntStream.range(0, allLegalMoves.size()).boxed().collect(Collectors.toMap(allLegalMoves::get, evaluations::get));
        final IPRModel model = new IPRModel(0.078, 0.502);
        final Map<Move, Double> map = model.getProjectedMoveProbabilities(moveEvaluations);
        System.out.println(map);
        System.out.println(map.values().stream().reduce(Double::max).orElseThrow(RuntimeException::new));
    }
}
