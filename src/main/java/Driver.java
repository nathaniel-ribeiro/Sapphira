import java.io.IOException;
import java.util.List;

public final class Driver {
    public static void main(final String[] args) throws IOException {
        final XiangqiRulesService xqRules = new XiangqiRulesService();
        final Board board = xqRules.makeMoves(Board.STARTING_BOARD,
                List.of(
                        new Move("h2", "e2"),
                        new Move("h9", "g7")
                )
        );
        System.out.println(Pikafish.INSTANCE.getBestMove(board));
        System.out.println(Pikafish.INSTANCE.evaluate(board));
    }
}
