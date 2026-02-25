class MoveClassifier {
    companion object {
        fun classify(winPercentDrop : Double) : MoveQuality{
            return when {
                winPercentDrop <= 0 -> MoveQuality.BEST
                winPercentDrop <= 2 -> MoveQuality.EXCELLENT
                winPercentDrop <= 5 -> MoveQuality.GOOD
                winPercentDrop <= 15 -> MoveQuality.INACCURACY
                winPercentDrop <= 20 -> MoveQuality.MISTAKE
                else -> MoveQuality.BLUNDER
            }
        }
    }
}