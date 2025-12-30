import java.io.*;

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
        reader.lines()
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

    public synchronized Move getBestMove(final Board board){
        return null;
    }

    public static synchronized Pikafish getInstance(){
        if(INSTANCE != null) return INSTANCE;
        INSTANCE = new Pikafish(ConfigOptions.getInstance());
        return INSTANCE;
    }
}
