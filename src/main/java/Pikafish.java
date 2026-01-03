import com.google.common.collect.ImmutableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Pikafish{
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final int nodesToSearch;

    private static final int MIN_THREADS = 1;
    private static final int MAX_THREADS = 1024;
    private static final int MIN_HASH_SIZE_MIB = 1;
    private static final int MAX_HASH_SIZE_MIB = 33554432;
    private static final int MIN_NODES_TO_SEARCH = 1;

    private static final Pattern BEST_MOVE_PATTERN = Pattern.compile("bestmove ([a-i]\\d)([a-i]\\d)(?:\\s+.*)?");
    private static final Pattern FEN_EXTRACTOR_PATTERN = Pattern.compile("Fen: (.+)");
    private static final Pattern LEGAL_MOVE_PATTERN = Pattern.compile("([a-i]\\d)([a-i]\\d): 1");
    private static final Pattern SCORE_CP_PATTERN = Pattern.compile(".*score cp (-?M?(\\d+)).*");

    public Pikafish(final PikafishOptions options){
        if(options.getNumThreads() < MIN_THREADS || options.getNumThreads() > MAX_THREADS)
            throw new IllegalArgumentException(String.format("Number of threads must be between %d and %d", MIN_THREADS, MAX_THREADS));
        if(options.getHashSizeMiB() < MIN_HASH_SIZE_MIB || options.getHashSizeMiB() > MAX_HASH_SIZE_MIB)
            throw new IllegalArgumentException(String.format("Hash size in MiB must be between %d and %d", MIN_HASH_SIZE_MIB, MAX_HASH_SIZE_MIB));
        if(options.getNodesToSearch() < MIN_NODES_TO_SEARCH)
            throw new IllegalArgumentException(String.format("Nodes to search must be at least %d", MIN_NODES_TO_SEARCH));

        this.nodesToSearch = options.getNodesToSearch();

        final ProcessBuilder processBuilder = new ProcessBuilder(options.getPathToExecutable());
        try{
            final Process process = processBuilder.start();
            this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            this.writer = new PrintWriter(process.getOutputStream(), true);
        }
        catch(final IOException exception){
            throw new RuntimeException(exception);
        }
        this.send("uci");
        final String ignored1 = this.waitForResponseContaining("uciok");
        this.send("isready");
        final String ignored2 = this.waitForResponseContaining("readyok");
        this.send("setoption name Threads value " + options.getNumThreads());
        this.send("setoption name Hash value " + options.getHashSizeMiB());
    }

    private void send(final String command){
        this.writer.println(command);
    }

    /**
     * Blocks until given keyword appears in the process's output buffer
     * @param keyword Keyword to wait for
     * @return the line containing the specified keyword
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

    public Move getBestMove(final Board board){
        this.send("position fen " + board.getFen());
        this.send("go nodes " + this.nodesToSearch);
        final String bestMoveLine = this.waitForResponseContaining("bestmove");
        final Matcher matcher = BEST_MOVE_PATTERN.matcher(bestMoveLine);
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
        if(moves.isEmpty()) return board;
        final String movesString = String.join(" ", moves.stream().map(Move::toString).toList());
        this.send("position fen " + board.getFen() + " moves " + movesString);
        // NOTE: this is a Stockfish/Pikafish specific command to display the board/get the final FEN.
        // It is NOT a command guaranteed by the UCI protocol.
        // For compatibility with other UCI engines, this code should be changed.
        this.send("d");
        final String fenLine = this.waitForResponseContaining("Fen:");
        final Matcher matcher = FEN_EXTRACTOR_PATTERN.matcher(fenLine);
        final boolean b = matcher.matches();
        if(!b) throw new RuntimeException();
        final String fen = matcher.group(1);
        return new Board(fen);
    }

    public double evaluate(final Board board) {
        this.send("position fen " + board.getFen());
        this.send("go nodes " + this.nodesToSearch);
        int evaluationCentipawns = 0;
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                final Matcher matcher = SCORE_CP_PATTERN.matcher(line);
                if (line.contains("bestmove")) break;
                if (matcher.matches()) {
                    try {
                        evaluationCentipawns = Integer.parseInt(matcher.group(1));
                    } catch (final NumberFormatException exception) {
                        // this catch block will proc if the evaluation is checkmate-in-n rather than centipawns
                        if (matcher.group(1).startsWith("M")) {
                            final int pliesTilMate = Integer.parseInt(matcher.group(2));
                            // we would rather make haste if we are doing the checkmating
                            evaluationCentipawns = 10_000 - pliesTilMate;
                        } else if (matcher.group(1).startsWith("-M")) {
                            final int pliesTilMate = Integer.parseInt(matcher.group(2));
                            // we would rather delay the inevitable if we are getting checkmated
                            evaluationCentipawns = -10_000 + pliesTilMate;
                        } else throw new RuntimeException();
                    }
                }
            }
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        // convert to pawns
        return evaluationCentipawns / 100.0;
    }

    public List<Move> getLegalMoves(final Board board) {
        this.send("position fen " + board.getFen());
        // NOTE: this is a Stockfish/Pikafish specific command to display the board/get the final FEN.
        // It is NOT a command guaranteed by the UCI protocol.
        // For compatibility with other UCI engines, this code should be changed.
        this.send("go perft 1");
        final List<Move> moves = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                final Matcher matcher = LEGAL_MOVE_PATTERN.matcher(line);
                if(line.contains("Nodes searched")) break;
                if (matcher.matches()) {
                    moves.add(new Move(matcher.group(1), matcher.group(2)));
                }
            }
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        return ImmutableList.copyOf(moves);
    }
}
