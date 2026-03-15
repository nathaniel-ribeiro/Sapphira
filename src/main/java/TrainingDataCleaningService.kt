import org.apache.commons.text.similarity.JaroWinklerSimilarity

class TrainingDataCleaningService {
    fun shouldRetain(game : Game) : Boolean {
        val neitherPlayerGuest = !game.redPlayer.isGuest && !game.blackPlayer.isGuest
        val notTooShortOrLong = game.moves.size in MIN_PLIES..MAX_PLIES
        val usernamesSufficientlyDifferent = JaroWinklerSimilarity().apply(game.redPlayer.username, game.blackPlayer.username) < MAX_USERNAME_SIMILARITY
        return neitherPlayerGuest &&
                notTooShortOrLong &&
                usernamesSufficientlyDifferent
    }

    companion object {
        const val MIN_PLIES = 30
        const val MAX_PLIES = 80
        const val MAX_USERNAME_SIMILARITY = 0.8
    }
}