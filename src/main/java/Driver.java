import java.io.IOException;
import java.util.List;

public final class Driver {
    public static void main(final String[] args) throws IOException {
        final Pikafish pikafish = new Pikafish(ConfigOptions.INSTANCE);
        final Board board = Board.STARTING_BOARD;
        final List<Move> allLegalMoves = pikafish.getLegalMoves(board);

        System.out.println(allLegalMoves.stream()
                    .map(move -> pikafish.makeMove(board, move))
                    .map(pikafish::evaluate)
                    .map(d -> -1 * d)
                    // flip evaluation to be from our perspective
                    .toList());
    }
}
