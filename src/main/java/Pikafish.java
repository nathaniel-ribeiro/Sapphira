import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Pikafish {
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private static Pikafish INSTANCE;

    private static final int NUM_THREADS = 15;
    private static final int HASH_SIZE_MB = 1000;
    private static final int NODES_TO_SEARCH = 1000000;

    private Pikafish(final String pathToExecutable){
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
        this.sendCommand("setoption name Threads value " + NUM_THREADS);
        this.sendCommand("setoption name Hash value " + HASH_SIZE_MB);
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
        this.sendCommand("go nodes " + NODES_TO_SEARCH);
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

    public static Pikafish getInstance(){
        if(INSTANCE != null)
            return INSTANCE;
        final ConfigOptions options = ConfigOptions.getInstance();
        INSTANCE = new Pikafish(options.getPathToExecutable());
        return INSTANCE;
    }
}
