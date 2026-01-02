import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Pikafish {
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private final int numThreads;
    private final int hashSizeMiB;
    private final int nodesToSearch;

    private static final int MIN_THREADS = 1;
    private static final int MAX_THREADS = 1024;
    private static final int MIN_HASH_SIZE_MIB = 1;
    private static final int MAX_HASH_SIZE_MIB = 33554432;
    private static final int MIN_NODES_TO_SEARCH = 1;

    public static final Pikafish INSTANCE = new Pikafish(ConfigOptions.getInstance().getPathToExecutable(),
                                                          ConfigOptions.getInstance().getNumThreads(),
                                                          ConfigOptions.getInstance().getHashSizeMiB(),
                                                          ConfigOptions.getInstance().getNodesToSearch());

    private Pikafish(final String pathToExecutable, final int numThreads, final int hashSizeMiB, final int nodesToSearch){
        if(numThreads < MIN_THREADS || numThreads > MAX_THREADS)
            throw new IllegalArgumentException(String.format("Number of threads must be between %d and %d", MIN_THREADS, MAX_THREADS));
        if(hashSizeMiB < MIN_HASH_SIZE_MIB || hashSizeMiB > MAX_HASH_SIZE_MIB)
            throw new IllegalArgumentException(String.format("Hash size in MiB must be between %d and %d", MIN_HASH_SIZE_MIB, MAX_HASH_SIZE_MIB));
        if(nodesToSearch < MIN_NODES_TO_SEARCH)
            throw new IllegalArgumentException(String.format("Nodes to search must be at least %d", MIN_NODES_TO_SEARCH));

        this.numThreads = numThreads;
        this.hashSizeMiB = hashSizeMiB;
        this.nodesToSearch = nodesToSearch;

        final ProcessBuilder processBuilder = new ProcessBuilder(pathToExecutable);
        try{
            final Process process = processBuilder.start();
            this.bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final OutputStream outputStream = process.getOutputStream();
            this.printWriter = new PrintWriter(outputStream);
        }
        catch(final IOException exception){
            throw new RuntimeException(exception);
        }
        this.sendCommand("uci");
        this.waitForResponseContaining("uciok");
        this.sendCommand("isready");
        this.waitForResponseContaining("readyok");
        this.sendCommand("setoption name Threads value " + this.numThreads);
        this.sendCommand("setoption name Hash value " + this.hashSizeMiB);
    }

    /**
     * Blocks until given keyword appears in the process's output buffer
     * @param keyword Keyword to wait for
     */
    private void waitForResponseContaining(final String keyword) {
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(keyword)) {
                    return;
                }
            }
            throw new RuntimeException("Process terminated before keyword: '" + keyword + "' was received.");
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    private void sendCommand(final String command){
        printWriter.println(command);
        printWriter.flush();
    }

    public Move getBestMove(final Board board) throws IOException {
        this.sendCommand("position fen " + board.getFen());
        this.sendCommand("go nodes " + this.nodesToSearch);
        String line;
        while ((line = this.bufferedReader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                final Pattern pattern = Pattern.compile("bestmove ([a-i]\\d)([a-i]\\d)(?:\\s+.*)?");
                final Matcher matcher = pattern.matcher(line);
                final boolean b = matcher.matches();
                // group 0 is the whole line, group 1 is the best move group
                if (!b) throw new RuntimeException("No best move could be found");
                final String srcSquare = matcher.group(1);
                final String destSquare = matcher.group(2);
                return new Move(srcSquare, destSquare);
            }
        }
        throw new RuntimeException("No best move could be found");
    }
}
