import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Move {
    private final String srcSquare;
    private final String destSquare;

    public Move(final String srcSquare, final String destSquare){
        final Pattern pattern = Pattern.compile("[a-i]\\d");
        Matcher matcher = pattern.matcher(srcSquare);
        if(!matcher.matches())
            throw new IllegalArgumentException("Source square must be a-i followed by a number (e.g. i0).");
        matcher = pattern.matcher(destSquare);
        if(!matcher.matches())
            throw new IllegalArgumentException("Destination square must be a-i followed by a number (e.g. i0).");
        if(srcSquare.equalsIgnoreCase(destSquare))
            throw new IllegalArgumentException("Source square cannot be the same as destination square.");

        this.srcSquare = srcSquare.toLowerCase();
        this.destSquare = destSquare.toLowerCase();
    }

    public String getSrcSquare() {
        return this.srcSquare;
    }

    public String getDestSquare() {
        return this.destSquare;
    }

    @Override
    public String toString(){
        return this.srcSquare + this.destSquare;
    }

    @Override
    public boolean equals(final Object other){
        if(other == null) return false;
        if(this == other) return true;
        if(other instanceof Move otherMove){
            return this.srcSquare.equalsIgnoreCase(otherMove.srcSquare) &&
                    this.destSquare.equalsIgnoreCase(otherMove.destSquare);
        }
        return false;
    }

    @Override
    public int hashCode(){
        int result = this.srcSquare.toLowerCase().hashCode();
        result = result * 31 + this.destSquare.toLowerCase().hashCode();
        return result;
    }
}
