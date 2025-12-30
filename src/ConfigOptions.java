import java.io.IOException;
import java.util.Properties;

public class ConfigOptions {
    private final String pathToExecutable;
    private final int numThreads;
    private final int hashSizeMB;
    private static ConfigOptions INSTANCE;

    private ConfigOptions(){
        final Properties properties = new Properties();
        try {
            properties.load(ConfigOptions.class.getClassLoader().getResourceAsStream("config.properties"));
            this.pathToExecutable = properties.getProperty("pathToExecutable");
            this.numThreads = Integer.parseInt(properties.getProperty("numThreads"));
            this.hashSizeMB = Integer.parseInt(properties.getProperty("hashSizeMB"));
        }
        catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public static synchronized ConfigOptions getInstance(){
        if(INSTANCE != null) return INSTANCE;
        INSTANCE = new ConfigOptions();
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
