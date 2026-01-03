import java.io.IOException;
import java.util.List;

public final class Driver {
    public static void main(final String[] args) throws IOException {
        final Pikafish pikafish = new Pikafish(ConfigOptions.INSTANCE);
        final Board board = pikafish.makeMoves(
                Board.STARTING_BOARD,
                List.of(
                        new Move("h2", "h9")
                )
        );
        System.out.println(pikafish.evaluate(board));
    }
}
