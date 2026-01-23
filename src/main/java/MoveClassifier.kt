class MoveClassifier {
    fun classify(dropInWinProbability : Double) : MoveQuality{
        return when {
            dropInWinProbability <= 0.0 -> MoveQuality.BEST
            dropInWinProbability <= 0.02 -> MoveQuality.EXCELLENT
            dropInWinProbability <= 0.05 -> MoveQuality.GOOD
            dropInWinProbability <= 0.15 -> MoveQuality.INACCURACY
            dropInWinProbability <= 0.20 -> MoveQuality.MISTAKE
            else -> MoveQuality.BLUNDER
        }
    }
}