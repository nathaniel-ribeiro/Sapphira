interface IFeature {
    fun extract(reviewedGame : ReviewedGame, alliance : Alliance) : Double?
}