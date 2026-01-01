import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Board {
    private final String fen;
    private final Alliance alliance;
    private final int pliesSinceACapture;
    private final int pliesSinceStart;

    public Board(final String fen){
        // NOTE: this check for valid FEN is not rigorous.
        // The following regex will match things that look like FEN but are in fact nonsensical positions
        final Pattern pattern = Pattern.compile("([kabrcnpKABRCNP0-9]+/?){10} ([wb]) - - (\\d+) (\\d+)");
        final Matcher matcher = pattern.matcher(fen);
        if(!matcher.matches())
            throw new IllegalArgumentException("Invalid FEN");
        this.fen = fen;
        this.alliance = matcher.group(2).equals("w") ? Alliance.RED : Alliance.BLACK;
        this.pliesSinceACapture = Integer.parseInt(matcher.group(3));
        this.pliesSinceStart = Integer.parseInt(matcher.group(4));
    }

    public String getFen() {
        return fen;
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public int getPliesSinceACapture() {
        return pliesSinceACapture;
    }

    public int getPliesSinceStart() {
        return pliesSinceStart;
    }

    @Override
    public String toString() {
        return "Board{" + "fen='" + fen + '\'' +
                ", alliance=" + alliance +
                ", pliesSinceACapture=" + pliesSinceACapture +
                ", pliesSinceStart=" + pliesSinceStart +
                '}';
    }

    @Override
    public boolean equals(final Object other){
        if(other == null) return false;
        if(this == other) return true;
        if(other instanceof Board otherBoard){
            return this.fen.equals(otherBoard.fen) &&
                    this.alliance == otherBoard.alliance &&
                    this.pliesSinceStart == otherBoard.pliesSinceStart &&
                    this.pliesSinceACapture == otherBoard.pliesSinceACapture;
        }
        return false;
    }

    @Override
    public int hashCode(){
        int result = this.fen.hashCode();
        result = result * 31 + this.alliance.hashCode();
        result = result * 31 + Integer.hashCode(this.pliesSinceACapture);
        result = result * 31 + Integer.hashCode(this.pliesSinceStart);
        return result;
    }
}
