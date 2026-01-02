import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Move(String srcSquare, String destSquare) {
    public Move(final String srcSquare, final String destSquare) {
        //NOTE: this is a necessary but insufficient check for legal moves
        final Pattern pattern = Pattern.compile("[a-i]\\d");
        Matcher matcher = pattern.matcher(srcSquare);
        if (!matcher.matches())
            throw new IllegalArgumentException("Source square must be a-i followed by a number (e.g. i0).");
        matcher = pattern.matcher(destSquare);
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
