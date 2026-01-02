import java.io.IOException;
import java.util.Properties;

public final class ConfigOptions {
    private final String pathToExecutable;
    private static ConfigOptions INSTANCE;

    private ConfigOptions(final String pathToExecutable){
        this.pathToExecutable = pathToExecutable;
    }
    public static synchronized ConfigOptions getInstance(){
        if(INSTANCE != null)
            return INSTANCE;
        final Properties properties = new Properties();
        try {
            properties.load(ConfigOptions.class.getClassLoader().getResourceAsStream("config.properties"));
            final String pathToExecutable = properties.getProperty("pathToExecutable");
            INSTANCE = new ConfigOptions(pathToExecutable);
        }
        catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
        return INSTANCE;
    }

    public String getPathToExecutable() {
        return pathToExecutable;
    }
}
