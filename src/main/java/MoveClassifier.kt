class MoveClassifier {
    fun classify(accuracyPercent : Double) : MoveQuality{
        return when {
            accuracyPercent >= 99.9 -> MoveQuality.BEST
            accuracyPercent > 91 -> MoveQuality.EXCELLENT
            accuracyPercent > 80 -> MoveQuality.GOOD
            accuracyPercent > 63 -> MoveQuality.INACCURACY
            accuracyPercent > 40 -> MoveQuality.MISTAKE
            else -> MoveQuality.BLUNDER
        }
    }
}