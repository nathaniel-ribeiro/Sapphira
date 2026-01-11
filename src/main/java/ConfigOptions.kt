import java.util.*

object ConfigOptions : PikafishOptions, FeatureExtractionOptions {
    override val pathToExecutable: String
    override val numThreads: Int
    override val hashSizeMiB: Int
    override val nodesToSearch: Int
    override val winningAdvantageThreshold: Int
    override val numberOfPliesToExclude: Int

    init {
        val properties = Properties()
        val stream = requireNotNull(
            ConfigOptions::class.java.getClassLoader().getResourceAsStream("config.properties")
        ){ "config.properties not found on classpath" }
        properties.load(stream)
        this.pathToExecutable = properties.getProperty("pathToExecutable")
        this.numThreads = properties.getProperty("numThreads").toInt()
        this.hashSizeMiB = properties.getProperty("hashSizeMiB").toInt()
        this.nodesToSearch = properties.getProperty("nodesToSearch").toInt()
        this.winningAdvantageThreshold = properties.getProperty("winningAdvantageThreshold").toInt()
        this.numberOfPliesToExclude = properties.getProperty("numberOfPliesToExclude").toInt()
    }
}
