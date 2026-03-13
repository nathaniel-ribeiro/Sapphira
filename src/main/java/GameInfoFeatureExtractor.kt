class GameInfoFeatureExtractor : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame, alliance : Alliance): Map<String, Double?> {
        val player = if(alliance == Alliance.RED) reviewedGame.game.redPlayer else reviewedGame.game.blackPlayer
        return mapOf(
            "Game Length" to reviewedGame.game.moves.size.toDouble(),
            "Rating" to player.rating.toDouble(),
            "Game Timer" to reviewedGame.game.gameTimer.toDouble(),
            "Move Timer" to reviewedGame.game.moveTimer.toDouble(),
            "Increment" to reviewedGame.game.increment.toDouble()
        )
    }
}