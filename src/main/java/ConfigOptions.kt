import java.util.*

object ConfigOptions : PikafishOptions, DataProcessingOptions {
    override val numThreads: Int
    override val hashSizeMiB: Int
    override val nodesToSearch: Int
    override val pikafishPoolSize : Int

    init {
        val properties = Properties()
        val stream = requireNotNull(
            ConfigOptions::class.java.getClassLoader().getResourceAsStream("config.properties")
        ){ "config.properties not found on classpath" }
        properties.load(stream)
        this.numThreads = properties.getProperty("numThreads").toInt()
        this.hashSizeMiB = properties.getProperty("hashSizeMiB").toInt()
        this.nodesToSearch = properties.getProperty("nodesToSearch").toInt()
        this.pikafishPoolSize = properties.getProperty("pikafishPoolSize").toInt()
    }
}
