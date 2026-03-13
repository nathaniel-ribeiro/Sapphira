class GameAccuracyFeatureExtractor : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame, alliance : Alliance): Map<String, Double?> {
        val movesForAlliance = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == alliance }
        // NOTE: a simple average is used here for legacy/compatibility reasons with Xiangqi.com's source code.
        // a more robust calculation is described here: https://lichess.org/page/accuracy
        val accuracy = movesForAlliance.map { it.moveAccuracy }.average()
        return mapOf("Accuracy" to accuracy)
    }
}