interface IFeatureProvider {
    fun extract(reviewedGame : ReviewedGame) : Map<String, Double?>
}