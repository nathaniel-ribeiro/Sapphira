import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Move(String srcSquare, String destSquare) {
    private static final Pattern SQUARE_PATTERN = Pattern.compile("[a-i]\\d");
    public Move(final String srcSquare, final String destSquare) {
        //NOTE: this is a necessary but insufficient check for legal moves
        Matcher matcher = SQUARE_PATTERN.matcher(srcSquare);
        if (!matcher.matches())
            throw new IllegalArgumentException("Source square must be a-i followed by a number (e.g. i0).");
        matcher = SQUARE_PATTERN.matcher(destSquare);
        if (!matcher.matches())
            throw new IllegalArgumentException("Destination square must be a-i followed by a number (e.g. i0).");
        if (srcSquare.equalsIgnoreCase(destSquare))
            throw new IllegalArgumentException("Source square cannot be the same as destination square.");

        this.srcSquare = srcSquare.toLowerCase();
        this.destSquare = destSquare.toLowerCase();
    }

    @Override
    public String toString() {
        return this.srcSquare + this.destSquare;
    }
}
