interface IFeatureProvider {
    fun extract(reviewedGame : ReviewedGame, alliance : Alliance) : Map<String, Double?>
    fun getFeatureNames() : List<String>
}