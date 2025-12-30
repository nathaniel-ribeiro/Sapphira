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
        //TODO: set # of threads and hash size by sending commands to process
        this.sendCommand("uci");
        this.waitFor("uciok");
        this.sendCommand("isready");
        this.waitFor("readyok");
        this.sendCommand("setoption name Threads value " + options.getNumThreads());
        this.sendCommand("setoption name Hash value " + options.getHashSizeMB());
    }

    /**
     * Send an arbitrary command to the running Pikafish process (thread-safe).
     * @param command command to send to Pikafish. See Stockfish documentation for valid commands.
     * @return full output of Pikafish in response to the given command.
     */
    private synchronized String sendCommand(final String command){
        final OutputStream outputStream = this.process.getOutputStream();
        final PrintWriter printWriter = new PrintWriter(outputStream, true);
        printWriter.println(command);
        printWriter.close();
        return null;
    }

    public synchronized Move getBestMove(final Board board){

    }

    public static synchronized Pikafish getInstance(){
        if(INSTANCE != null) return INSTANCE;
        INSTANCE = new Pikafish(ConfigOptions.getInstance());
        return INSTANCE;
    }
}
