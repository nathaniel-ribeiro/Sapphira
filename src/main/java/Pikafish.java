import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Pikafish {
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final int nodesToSearch;

    private static final int MIN_THREADS = 1;
    private static final int MAX_THREADS = 1024;
    private static final int MIN_HASH_SIZE_MIB = 1;
    private static final int MAX_HASH_SIZE_MIB = 33554432;
    private static final int MIN_NODES_TO_SEARCH = 1;

    public static final Pikafish INSTANCE = new Pikafish(ConfigOptions.INSTANCE.getPathToExecutable(),
                                                          ConfigOptions.INSTANCE.getNumThreads(),
                                                          ConfigOptions.INSTANCE.getHashSizeMiB(),
                                                          ConfigOptions.INSTANCE.getNodesToSearch());

    private Pikafish(final String pathToExecutable, final int numThreads, final int hashSizeMiB, final int nodesToSearch){
        if(numThreads < MIN_THREADS || numThreads > MAX_THREADS)
            throw new IllegalArgumentException(String.format("Number of threads must be between %d and %d", MIN_THREADS, MAX_THREADS));
        if(hashSizeMiB < MIN_HASH_SIZE_MIB || hashSizeMiB > MAX_HASH_SIZE_MIB)
            throw new IllegalArgumentException(String.format("Hash size in MiB must be between %d and %d", MIN_HASH_SIZE_MIB, MAX_HASH_SIZE_MIB));
        if(nodesToSearch < MIN_NODES_TO_SEARCH)
            throw new IllegalArgumentException(String.format("Nodes to search must be at least %d", MIN_NODES_TO_SEARCH));

        this.nodesToSearch = nodesToSearch;

        final ProcessBuilder processBuilder = new ProcessBuilder(pathToExecutable);
        try{
            final Process process = processBuilder.start();
            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final OutputStream outputStream = process.getOutputStream();
            this.writer = new PrintWriter(outputStream, true);
        }
        catch(final IOException exception){
            throw new RuntimeException(exception);
        }
        this.writer.println("uci");
        final String ignored1 = this.waitForResponseContaining("uciok");
        this.writer.println("isready");
        final String ignored2 = this.waitForResponseContaining("readyok");
        this.writer.println("setoption name Threads value " + numThreads);
        this.writer.println("setoption name Hash value " + hashSizeMiB);
    }

    /**
     * Blocks until given keyword appears in the process's output buffer
     * @param keyword Keyword to wait for
     */
    private String waitForResponseContaining(final String keyword) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(keyword)) {
                    return line;
                }
            }
            throw new RuntimeException("Process terminated before keyword: '" + keyword + "' was received.");
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Move getBestMove(final Board board) throws IOException {
        this.writer.println("position fen " + board.getFen());
        this.writer.println("go nodes " + this.nodesToSearch);
        final String bestMoveLine = this.waitForResponseContaining("bestmove");
        final Pattern pattern = Pattern.compile("bestmove ([a-i]\\d)([a-i]\\d)(?:\\s+.*)?");
        final Matcher matcher = pattern.matcher(bestMoveLine);
        final boolean b = matcher.matches();
        if (!b) throw new RuntimeException();
        final String srcSquare = matcher.group(1);
        final String destSquare = matcher.group(2);
        return new Move(srcSquare, destSquare);
    }

    public Board makeMove(final Board board, final Move move){
        return this.makeMoves(board, List.of(move));
    }

    public Board makeMoves(final Board board, final List<Move> moves){
        final String movesString = String.join(" ", moves.stream().map(Move::toString).toList());
        this.writer.println("position fen " + board.getFen() + " moves " + movesString);
        // NOTE: this is a Stockfish/Pikafish specific command to display the board/get the final FEN.
        // It is NOT a command guaranteed by the UCI protocol.
        // For compatibility with other UCI engines, this code should be changed.
        this.writer.println("d");
        final String fenLine = this.waitForResponseContaining("Fen:");
        final Pattern pattern = Pattern.compile("Fen: (.+)");
        final Matcher matcher = pattern.matcher(fenLine);
        final boolean b = matcher.matches();
        if(!b) throw new RuntimeException();
        final String fen = matcher.group(1);
        return new Board(fen);
    }

    public int evaluate(final Board board){
        this.writer.println("position fen " + board.getFen());
        this.writer.println("go nodes " + this.nodesToSearch);
        //TODO
        return 0;
    }
}
