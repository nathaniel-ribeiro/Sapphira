class FeatureAggregationService(val featureProviders : List<IFeatureProvider>) {
    fun getFeatures(reviewedGame: ReviewedGame, alliance: Alliance) : DoubleArray{
        return featureProviders
            .map { it.extract(reviewedGame, alliance) }
            .flatMap { it.values }
            .map { it ?: Double.NaN }
            .toDoubleArray()
    }
}