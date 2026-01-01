import java.io.IOException;
import java.util.Properties;

public final class ConfigOptions {
    private final String pathToExecutable;
    private final int numThreads;
    private final int hashSizeMB;
    private static ConfigOptions INSTANCE;

    private ConfigOptions(final String pathToExecutable, final int numThreads, final int hashSizeMB){
        this.pathToExecutable = pathToExecutable;
        this.numThreads = numThreads;
        this.hashSizeMB = hashSizeMB;
    }
    public static synchronized ConfigOptions getInstance(){
        if(INSTANCE != null) return INSTANCE;
        final Properties properties = new Properties();
        try {
            properties.load(ConfigOptions.class.getClassLoader().getResourceAsStream("config.properties"));
            final String pathToExecutable = properties.getProperty("pathToExecutable");
            final int numThreads = Integer.parseInt(properties.getProperty("numThreads"));
            final int hashSizeMB = Integer.parseInt(properties.getProperty("hashSizeMB"));
            INSTANCE = new ConfigOptions(pathToExecutable, numThreads, hashSizeMB);
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

    public int getHashSizeMB() {
        return hashSizeMB;
    }
}
