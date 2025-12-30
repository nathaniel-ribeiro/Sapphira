import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pikafish {
    private final Process process;
    private static Pikafish INSTANCE;

    private Pikafish(final ConfigOptions options){
        final ProcessBuilder processBuilder = new ProcessBuilder(options.getPathToExecutable());
        try{
            this.process = processBuilder.start();
        }
        catch(final IOException exception){
            throw new RuntimeException(exception);
        }
        this.sendCommand("uci");
        this.waitForResponseContaining("uciok");
        this.sendCommand("isready");
        this.waitForResponseContaining("readyok");
        this.sendCommand("setoption name Threads value " + options.getNumThreads());
        this.sendCommand("setoption name Hash value " + options.getHashSizeMB());
    }

    /**
     * Blocks until given keyword appears in the process's output buffer
     * @param keyword Keyword to wait for
     */
    private void waitForResponseContaining(final String keyword) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final String ignored = reader.lines()
                                    .filter(line -> line.contains(keyword))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Keyword not found in process output"));
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

    public synchronized String getBestMove(final String fen){
        this.sendCommand("set position fen " + fen);
        //TODO: this is an arbitrary default thinking limit
        this.sendCommand("go nodes 5000000");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final String bestmoveLine = reader.lines()
                                        .filter(line -> line.contains("bestmove"))
                                        .findFirst()
                                        .orElseThrow(() -> new RuntimeException("No best move could be found."));

        final Pattern pattern = Pattern.compile("bestmove (([a-i]\\d){2}) ponder (([a-i]\\d){2})");
        final Matcher matcher = pattern.matcher(bestmoveLine);
        boolean b = matcher.matches();
        // group 0 is the whole line, group 1 is the best move group
        if(!b) throw new RuntimeException("No best move could be found");
        return matcher.group(1);
    }

    public static synchronized Pikafish getInstance(){
        if(INSTANCE != null) return INSTANCE;
        INSTANCE = new Pikafish(ConfigOptions.getInstance());
        return INSTANCE;
    }
}
