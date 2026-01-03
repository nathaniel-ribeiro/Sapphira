import java.io.IOException;
import java.util.List;

public final class Driver {
    public static void main(final String[] args) throws IOException {
        final Pikafish pikafish = new Pikafish(ConfigOptions.INSTANCE);
        final Board board = new Board("4k4/9/9/9/9/9/9/9/9/3KR4 b - - 0 1");
        System.out.println(pikafish.evaluate(board));
    }
}
