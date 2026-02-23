interface FeatureProvider {
    fun extract(reviewedGame : ReviewedGame) : Map<String, Double?>
}