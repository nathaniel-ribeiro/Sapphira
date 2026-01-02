import com.google.common.collect.ImmutableList;
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

    public Game {
        gameStates = ImmutableList.copyOf(gameStates);
        moves = ImmutableList.copyOf(moves);
    }

    public List<Board> getGameStates(){
        return ImmutableList.copyOf(this.gameStates);
    }

    public List<Move> getMoves(){
        return ImmutableList.copyOf(this.moves);
    }
}
