import com.google.common.collect.ImmutableList;

import java.util.List;

public class XiangqiRulesService {
    public Board makeMove(final Board board, final Move move){
        return this.makeMoves(board, List.of(move));
    }
    public Board makeMoves(final Board board, final List<Move> moves){
        return Pikafish.INSTANCE.makeMoves(board, moves);
    }
    public List<Move> getLegalMoves(final Board board){
        return ImmutableList.copyOf(Pikafish.INSTANCE.getLegalMoves(board));
    }
}
