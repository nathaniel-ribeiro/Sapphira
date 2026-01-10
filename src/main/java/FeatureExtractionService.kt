import kotlin.math.abs

class FeatureExtractionService(val pikafish: Pikafish, val options: FeatureExtractionOptions) {
    fun getAdjustedCPLosses(evaluationsRedPerspective : List<Double>, alliance: Alliance) : List<Int>{
        if(evaluationsRedPerspective.size <= options.numberOfPliesToExclude) throw RuntimeException("Game was too short to provide a centipawn loss")
        val evaluationsAfterOpening = evaluationsRedPerspective.drop(options.numberOfPliesToExclude - 1)
        val redCPLosses = ArrayList<Int>()
        val blackCPLosses = ArrayList<Int>()
        for(i in 0..<(evaluationsAfterOpening.size - 1)){
            if(abs(evaluationsAfterOpening[i]) >= options.winningAdvantageThreshold) continue
            val redTurn = i.mod(2) == 0
            val evalBefore =  evaluationsAfterOpening[i]
            val evalAfter = evaluationsAfterOpening[i + 1]
            val evalDifferenceRedPerspective = evalAfter - evalBefore
            val evalDifferenceBlackPerspective = -evalDifferenceRedPerspective
            if(redTurn) redCPLosses.add((evalDifferenceRedPerspective * 100).toInt())
            else blackCPLosses.add((evalDifferenceBlackPerspective * 100).toInt())
        }
        return if(alliance == Alliance.RED) redCPLosses else blackCPLosses
    }

    fun getLongestBestMoveStreak(){
        TODO()
    }

    fun getBlunderRate(){
        TODO()
    }

    fun countEvaluationPeaks(){
        TODO()
    }

    fun countEvaluationValleys(){
        TODO()
    }

    fun getThinkTimeMean(){
        TODO()
    }

    fun getThinkTimeStd(){
        TODO()
    }
}