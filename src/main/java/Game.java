import java.util.List;

public record Game(Player redPlayer, Player blackPlayer, List<Board> gameStates, List<Move> moves) {
}
