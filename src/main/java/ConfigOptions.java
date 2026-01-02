import java.io.IOException;
import java.util.Properties;

public final class ConfigOptions {
    private final String pathToExecutable;
    private final int numThreads;
    private final int hashSizeMiB;
    private final int nodesToSearch;

    public static final ConfigOptions INSTANCE = new ConfigOptions();

    private ConfigOptions(){
        final Properties properties = new Properties();
        try {
            properties.load(ConfigOptions.class.getClassLoader().getResourceAsStream("config.properties"));
            this.pathToExecutable = properties.getProperty("pathToExecutable");
            this.numThreads = Integer.parseInt(properties.getProperty("numThreads"));
            this.hashSizeMiB = Integer.parseInt(properties.getProperty("hashSizeMiB"));
            this.nodesToSearch = Integer.parseInt(properties.getProperty("nodesToSearch"));
        }
        catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getPathToExecutable() {
        return pathToExecutable;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public int getHashSizeMiB() {
        return hashSizeMiB;
    }

    public int getNodesToSearch() {
        return nodesToSearch;
    }
}
