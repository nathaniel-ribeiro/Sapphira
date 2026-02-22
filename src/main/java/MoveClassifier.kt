class MoveClassifier {
    companion object {
        fun classify(winPercentDrop : Double) : MoveQuality{
            return when {
                winPercentDrop <= 0.0 -> MoveQuality.BEST
                winPercentDrop <= 0.02 -> MoveQuality.EXCELLENT
                winPercentDrop <= 0.05 -> MoveQuality.GOOD
                winPercentDrop <= 0.15 -> MoveQuality.INACCURACY
                winPercentDrop <= 0.20 -> MoveQuality.MISTAKE
                else -> MoveQuality.BLUNDER
            }
        }
    }
}