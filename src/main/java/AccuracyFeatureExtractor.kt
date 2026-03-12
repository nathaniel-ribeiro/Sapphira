class AccuracyFeatureExtractor : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame): Map<String, Double?> {
        val redMoves = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == Alliance.RED }
        val blackMoves = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == Alliance.BLACK }
        // NOTE: a simple average is used here for legacy/compatibility reasons with Xiangqi.com's source code.
        // a better calculation is described here: https://lichess.org/page/accuracy
        val redAccuracy = redMoves.map { it.moveAccuracy }.average()
        val blackAccuracy = blackMoves.map { it.moveAccuracy }.average()
        return mapOf("Red Accuracy" to redAccuracy, "Black Accuracy" to blackAccuracy)
    }
}