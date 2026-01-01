import java.util.List;

public record Game(String uuid,
                   Player redPlayer,
                   Player blackPlayer,
                   int gameTimer,
                   int moveTimer,
                   int increment,
                   List<Board> gameStates,
                   List<Move> moves,
                   GameResult resultRed,
                   GameResult resultBlack,
                   GameResultReason gameResultReason) {
}
