import kotlin.math.abs

class FeatureExtractionService(val pikafish: Pikafish, val options: FeatureExtractionOptions) {
    fun getAdjustedCPLosses(evaluationsRedPerspective : List<Double>, alliance: Alliance) : List<Int>{
        require(evaluationsRedPerspective.size > options.numberOfPliesToExclude) { "Game was too short to provide a centipawn loss"}
        val evaluationsAfterOpening = evaluationsRedPerspective.drop(options.numberOfPliesToExclude - 1)
        val redCPLosses = ArrayList<Int>()
        val blackCPLosses = ArrayList<Int>()
        for(i in 0..<(evaluationsAfterOpening.size - 1)){
            val evalBefore =  evaluationsAfterOpening[i]
            val evalAfter = evaluationsAfterOpening[i + 1]
            if(abs(evalBefore) >= options.winningAdvantageThreshold && abs(evalAfter) >= options.winningAdvantageThreshold) continue
            val redTurn = i.mod(2) == 0
            val evalDropRed = evalBefore - evalAfter
            val evalDropBlack = -evalDropRed
            if(redTurn) redCPLosses.add((evalDropRed * 100).toInt())
            else blackCPLosses.add((evalDropBlack * 100).toInt())
        }
        return if(alliance == Alliance.RED) redCPLosses else blackCPLosses
    }

    fun getMoveQualityFrequencies(adjustedCPLosses : List<Int>) : Map<MoveQuality, Int>{
        require(adjustedCPLosses.isNotEmpty())
        val moveQualities = MoveQuality.entries.toTypedArray().toList()
        val moveQualityFrequencies = moveQualities.associateWith { 0 }.toMutableMap()
        for(cpLoss in adjustedCPLosses){
            for(moveQuality in moveQualities){
                if(cpLoss in moveQuality.cpLossRange){
                    moveQualityFrequencies[moveQuality] = (moveQualityFrequencies[moveQuality]?:0) + 1
                }
            }
        }
        return moveQualityFrequencies
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