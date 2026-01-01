import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Pikafish {
    private final Process process;
    private final BufferedReader bufferedReader;
    private static Pikafish INSTANCE;

    private static final int MIN_THREADS = 1;
    private static final int MAX_THREADS = 1024;
    private static final int MIN_HASH_SIZE_MB = 1;
    private static final int MAX_HASH_SIZE_MB = 33554432;

    private Pikafish(final String pathToExecutable, final int numThreads, final int hashSizeMB){
        if(numThreads < MIN_THREADS || numThreads > MAX_THREADS)
            throw new IllegalArgumentException(String.format("Num threads must be between %d and %d.", MIN_THREADS, MAX_THREADS));
        if(hashSizeMB < MIN_HASH_SIZE_MB || hashSizeMB > MAX_HASH_SIZE_MB)
            throw new IllegalArgumentException(String.format("Hash size (in MB) must be between %d and %d.", MIN_HASH_SIZE_MB, MAX_HASH_SIZE_MB));
        final ProcessBuilder processBuilder = new ProcessBuilder(pathToExecutable);
        try{
            this.process = processBuilder.start();
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
        }
        catch(final IOException exception){
            throw new RuntimeException(exception);
        }
        this.sendCommand("uci");
        this.waitForResponseContaining("uciok");
        this.sendCommand("isready");
        this.waitForResponseContaining("readyok");
        this.sendCommand("setoption name Threads value " + numThreads);
        this.sendCommand("setoption name Hash value " + hashSizeMB);
    }

    /**
     * Blocks until given keyword appears in the process's output buffer
     * @param keyword Keyword to wait for
     */
    private void waitForResponseContaining(final String keyword) {
        final String ignored = this.bufferedReader.lines()
                                                .filter(line -> line.contains(keyword))
                                                .findFirst()
                                                .orElseThrow(RuntimeException::new);
    }

    /**
     * Send an arbitrary command to the running Pikafish process (thread-safe).
     * @param command command to send to Pikafish. See Stockfish documentation for valid commands.
     * @return full output of Pikafish in response to the given command.
     */
    private synchronized void sendCommand(final String command){
        final OutputStream outputStream = this.process.getOutputStream();
        final PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(command);
        printWriter.flush();
    }

    public synchronized Move getBestMove(final Board board) throws IOException {
        this.sendCommand("set position fen " + board.getFen());
        //TODO: this is an arbitrary default thinking limit
        this.sendCommand("go movetime 500");
        String line;
        while ((line = this.bufferedReader.readLine()) != null) {
            if (line.startsWith("bestmove")) {
                final Pattern pattern = Pattern.compile("bestmove ([a-i]\\d)([a-i]\\d) ponder ([a-i]\\d)([a-i]\\d)");
                final Matcher matcher = pattern.matcher(line);
                boolean b = matcher.matches();
                // group 0 is the whole line, group 1 is the best move group
                if (!b) throw new RuntimeException("No best move could be found");
                final String srcSquare = matcher.group(1);
                final String destSquare = matcher.group(2);
                return new Move(srcSquare, destSquare);
            }
        }
        throw new RuntimeException("No best move could be found");
    }

    public static synchronized Pikafish getInstance(){
        if(INSTANCE != null)
            return INSTANCE;
        final ConfigOptions options = ConfigOptions.getInstance();
        INSTANCE = new Pikafish(options.getPathToExecutable(), options.getNumThreads(), options.getHashSizeMB());
        return INSTANCE;
    }
}
