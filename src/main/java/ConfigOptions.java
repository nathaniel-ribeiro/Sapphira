import java.io.IOException;
import java.util.Properties;

public final class ConfigOptions implements PikafishOptions, IntrinsicPerformanceRatingOptions {
    private final String pathToExecutable;
    private final int numThreads;
    private final int hashSizeMiB;
    private final int nodesToSearch;
    private final double winningAdvantageThreshold;
    private final int numberOfFirstTurnsToExclude;

    public static final ConfigOptions INSTANCE = new ConfigOptions();

    private ConfigOptions(){
        final Properties properties = new Properties();
        try {
            properties.load(ConfigOptions.class.getClassLoader().getResourceAsStream("config.properties"));
            this.pathToExecutable = properties.getProperty("pathToExecutable");
            this.numThreads = Integer.parseInt(properties.getProperty("numThreads"));
            this.hashSizeMiB = Integer.parseInt(properties.getProperty("hashSizeMiB"));
            this.nodesToSearch = Integer.parseInt(properties.getProperty("nodesToSearch"));
            this.winningAdvantageThreshold = Double.parseDouble(properties.getProperty("winningAdvantageThreshold"));
            this.numberOfFirstTurnsToExclude = Integer.parseInt(properties.getProperty("numberOfFirstTurnsToExclude"));
        }
        catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String getPathToExecutable() {
        return pathToExecutable;
    }

    @Override
    public int getNumThreads() {
        return numThreads;
    }

    @Override
    public int getHashSizeMiB() {
        return hashSizeMiB;
    }

    @Override
    public int getNodesToSearch() {
        return nodesToSearch;
    }

    @Override
    public double getWinningAdvantageThreshold(){
        return this.winningAdvantageThreshold;
    }

    @Override
    public int getNumberOfFirstTurnsToExclude(){
        return this.numberOfFirstTurnsToExclude;
    }
}
