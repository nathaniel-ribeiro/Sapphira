class GameInfoFeatureExtractor : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame): Map<String, Double?> {
        return mapOf(
            "Game Length" to reviewedGame.game.moves.size.toDouble(),
            "Red Rating" to reviewedGame.game.redPlayer.rating.toDouble(),
            "Black Rating" to reviewedGame.game.blackPlayer.rating.toDouble(),
            "Game Timer" to reviewedGame.game.gameTimer.toDouble(),
            "Move Timer" to reviewedGame.game.moveTimer.toDouble(),
            "Increment" to reviewedGame.game.increment.toDouble()
        )
    }
}