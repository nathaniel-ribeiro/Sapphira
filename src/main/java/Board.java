import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Board {
    private final String fen;
    private final Alliance whoseTurn;
    private final int pliesSinceACapture;
    private final int fullMoveNumber;

    private static final String STARTING_FEN = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1";
    public static final Board STARTING_BOARD = new Board(STARTING_FEN);

    public Board(final String fen){
        // NOTE: this check for valid FEN is not rigorous.
        // The following regex will match things that look like FEN but are in fact nonsensical positions
        final Pattern pattern = Pattern.compile("([kabrcnpKABRCNP0-9]+/?){10} ([wb]) - - (\\d+) (\\d+)");
        final Matcher matcher = pattern.matcher(fen);
        if(!matcher.matches())
            throw new IllegalArgumentException("Invalid FEN");
        this.fen = fen;
        this.whoseTurn = matcher.group(2).equals("w") ? Alliance.RED : Alliance.BLACK;
        this.pliesSinceACapture = Integer.parseInt(matcher.group(3));
        this.fullMoveNumber = Integer.parseInt(matcher.group(4));
    }

    public String getFen() {
        return this.fen;
    }

    public Alliance getWhoseTurn() {
        return this.whoseTurn;
    }

    public int getPliesSinceACapture() {
        return this.pliesSinceACapture;
    }

    public int getFullMoveNumber() {
        return this.fullMoveNumber;
    }

    @Override
    public String toString() {
        return "Board{" + "fen='" + this.fen + '\'' +
                ", alliance=" + this.whoseTurn +
                ", pliesSinceACapture=" + this.pliesSinceACapture +
                ", pliesSinceStart=" + this.fullMoveNumber +
                '}';
    }

    @Override
    public boolean equals(final Object other){
        if(other == null) return false;
        if(this == other) return true;
        if(other instanceof Board otherBoard){
            return this.fen.equals(otherBoard.fen) &&
                    this.whoseTurn == otherBoard.whoseTurn &&
                    this.fullMoveNumber == otherBoard.fullMoveNumber &&
                    this.pliesSinceACapture == otherBoard.pliesSinceACapture;
        }
        return false;
    }

    @Override
    public int hashCode(){
        int result = this.fen.hashCode();
        result = result * 31 + this.whoseTurn.hashCode();
        result = result * 31 + Integer.hashCode(this.pliesSinceACapture);
        result = result * 31 + Integer.hashCode(this.fullMoveNumber);
        return result;
    }
}
