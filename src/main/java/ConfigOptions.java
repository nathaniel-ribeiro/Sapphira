import java.io.IOException;
import java.util.Properties;

public final class ConfigOptions {
    private final String pathToExecutable;
    private final int numThreads;
    private final int hashSizeMiB;
    private final int nodesToSearch;
    private static ConfigOptions INSTANCE;

    private ConfigOptions(final String pathToExecutable, final int numThreads,
                          final int hashSizeMiB, final int nodesToSearch){
        this.pathToExecutable = pathToExecutable;
        this.numThreads = numThreads;
        this.hashSizeMiB = hashSizeMiB;
        this.nodesToSearch = nodesToSearch;
    }
    public static ConfigOptions getInstance(){
        if(INSTANCE != null)
            return INSTANCE;
        final Properties properties = new Properties();
        try {
            properties.load(ConfigOptions.class.getClassLoader().getResourceAsStream("config.properties"));
            final String pathToExecutable = properties.getProperty("pathToExecutable");
            final int numThreads = Integer.parseInt(properties.getProperty("numThreads"));
            final int hashSizeMB = Integer.parseInt(properties.getProperty("hashSizeMiB"));
            final int nodesToSearch = Integer.parseInt(properties.getProperty("nodesToSearch"));
            INSTANCE = new ConfigOptions(pathToExecutable, numThreads, hashSizeMB, nodesToSearch);
        }
        catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        return INSTANCE;
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
