data class Features(
    val gameTimer : Int,
    val moveTimer : Int,
    val increment : Int,
    val usernameSimilarity : Double,
    val adjustedCPLossRed : Double,
    val adjustedCPLossBlack : Double,
    val longestBestOrExcellentStreakRed : Int,
    val longestBestOrExcellentStreakBlack : Int,
    val blunderRateRed : Double,
    val blunderRateBlack : Double,
    val averageBlunderInterarrivalTimeRed : Double,
    val averageBlunderInterarrivalTimeBlack : Double,
    val numReversals : Int,
    val areaUnderTheEvaluationCurve : Double,
    val recoveryRateRed : Double,
    val recoveryRateBlack : Double,
    val accuracyRed : Double,
    val accuracyBlack : Double,
    val gameLength : Int,
){
    init {
        require(gameTimer > 0)
        require(moveTimer > 0)
        require(increment >= 0)
        require(usernameSimilarity >= 0.0)
        require(longestBestOrExcellentStreakRed >= 0)
        require(longestBestOrExcellentStreakBlack >= 0)
        require(blunderRateRed >= 0.0)
        require(blunderRateBlack >= 0.0)
        require(averageBlunderInterarrivalTimeRed >= 0.0)
        require(averageBlunderInterarrivalTimeBlack >= 0.0)
        require(accuracyRed in 0.00..1.00)
        require(accuracyBlack in 0.00..1.00)
        require(gameLength > 0)
    }
}
