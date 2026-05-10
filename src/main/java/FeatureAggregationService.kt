class FeatureAggregationService(val features: List<Feature>) {
    val featureNames : List<String> get() = features.map { it.name }
    init {
        require(features.isNotEmpty()) { "Must pass at least one feature to aggregate." }
    }
    fun getFeatures(reviewedGame: ReviewedGame, alliance: Alliance) : DoubleArray {
        TODO()
    }
}