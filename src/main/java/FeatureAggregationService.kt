class FeatureAggregationService(val features: List<Feature>) {
    init {
        require(features.isNotEmpty()) { "Must pass at least one feature to aggregate." }
    }
    fun getFeatures(reviewedGame: ReviewedGame, alliance: Alliance) : DoubleArray{
        return features
            .map { it.calculate(reviewedGame, alliance) }
            .map { it?.toDouble() ?: Double.NaN }
            .toDoubleArray()
    }

    fun getFeatureNames() : List<String> {
        return features.map { it.name }
    }
}