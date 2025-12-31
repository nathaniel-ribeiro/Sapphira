import java.io.IOException;

public final class Driver {
    public static void main(final String[] args) throws IOException {
        System.out.println(Pikafish.getInstance().getBestMove("rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1"));
        System.out.println(Pikafish.getInstance().getBestMove("rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C4/9/RNBAKABNR b - - 0 1"));
    }
}
