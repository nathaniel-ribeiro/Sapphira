import java.util.List;

public class XiangqiRulesService {
    private final Pikafish pikafish;

    public XiangqiRulesService(final Pikafish pikafish){
        this.pikafish = pikafish;
    }
    public Board makeMove(final Board board, final Move move){
        return this.makeMoves(board, List.of(move));
    }
    public Board makeMoves(final Board board, final List<Move> moves){
        return this.pikafish.makeMoves(board, moves);
    }
    public List<Move> getLegalMoves(final Board board){
        return this.pikafish.getLegalMoves(board);
    }
}
