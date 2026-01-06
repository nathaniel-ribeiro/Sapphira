import java.io.IOException
import java.util.*

object ConfigOptions : PikafishOptions, IPROptions {
    override val pathToExecutable: String
    override val numThreads: Int
    override val hashSizeMiB: Int
    override val nodesToSearch: Int
    override val winningAdvantageThreshold: Double
    override val numberOfFirstTurnsToExclude: Int

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
        this.winningAdvantageThreshold = properties.getProperty("winningAdvantageThreshold").toDouble()
        this.numberOfFirstTurnsToExclude = properties.getProperty("numberOfFirstTurnsToExclude").toInt()
    }
}
