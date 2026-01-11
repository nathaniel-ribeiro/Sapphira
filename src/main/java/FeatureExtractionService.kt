import kotlin.math.abs

typealias CentipawnAndWinProbabilityLoss = Pair<Int, Double>
class FeatureExtractionService(val options: FeatureExtractionOptions) {
    fun getAdjustedEvaluationLosses(evaluationsRedPerspective: List<Evaluation>, alliance: Alliance) : List<CentipawnAndWinProbabilityLoss>{
        require(evaluationsRedPerspective.size > options.numberOfPliesToExclude) { "Game was too short to provide a centipawn loss"}
        val evaluationsAfterOpening = evaluationsRedPerspective.drop(options.numberOfPliesToExclude - 1)
        val redEvaluationLosses = ArrayList<CentipawnAndWinProbabilityLoss>()
        val blackEvaluationLosses = ArrayList<CentipawnAndWinProbabilityLoss>()
        for(i in 0..<(evaluationsAfterOpening.size - 1)){
            val before =  evaluationsAfterOpening[i]
            val after = evaluationsAfterOpening[i + 1]
            // TODO: check if win probability <= 10% or >= 90%
            if(abs(before.centipawns) >= options.winningAdvantageThreshold
                && abs(after.centipawns) >= options.winningAdvantageThreshold) continue
            val redTurn = i.mod(2) == 0
            val evalDropRed = CentipawnAndWinProbabilityLoss(before.centipawns - after.centipawns, before.winProbability - after.winProbability)
            val evalDropBlack = CentipawnAndWinProbabilityLoss(-evalDropRed.first, -evalDropRed.second)
            if(redTurn) redEvaluationLosses.add(evalDropRed)
            else blackEvaluationLosses.add(evalDropBlack)
        }
        return if(alliance == Alliance.RED) redEvaluationLosses else blackEvaluationLosses
    }

    fun getMoveQualityFrequencies(adjustedEvaluationLosses : List<CentipawnAndWinProbabilityLoss>) : Map<MoveQuality, Int>{
        require(adjustedEvaluationLosses.isNotEmpty())
        val moveQualities = MoveQuality.entries.toTypedArray().toList()
        val moveQualityFrequencies = moveQualities.associateWith { 0 }.toMutableMap()
        for(centipawnAndWinProbabilityLoss in adjustedEvaluationLosses){
            for(moveQuality in moveQualities){
                if(centipawnAndWinProbabilityLoss.second in moveQuality.dropInWinProbability){
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