import java.io.IOException;
import java.util.List;

public final class Driver {
    public static void main(final String[] args) throws IOException {
        final Pikafish pikafish = new Pikafish(ConfigOptions.INSTANCE);
        final XiangqiRulesService xqRules = new XiangqiRulesService(pikafish);
        final Board board = xqRules.makeMoves(
                Board.STARTING_BOARD,
                List.of(
                        new Move("h2", "e2"),
                        new Move("h9", "g7"),
                        new Move("h0", "g2"),
                        new Move("i9", "h9"),
                        new Move("g3", "g4")
                )
        );
        System.out.println(pikafish.getBestMove(board));
    }
}
