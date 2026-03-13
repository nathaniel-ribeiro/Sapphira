class FeatureAggregationService(val featureProviders : List<IFeatureProvider>) {
    fun getFeatures(reviewedGame: ReviewedGame, alliance: Alliance) : DoubleArray{
        val allFeatureMaps = featureProviders.map { it.extract(reviewedGame, alliance) }
        TODO()
    }
}