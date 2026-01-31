data class Features(
    val gameTimer : Int,
    val moveTimer : Int,
    val increment : Int,
    val resultRed : GameResult,
    val resultBlack : GameResult,
    val gameResultReason : GameResultReason,
    val usernameSimilarity : Double,
    val adjustedCPLossRed : Double,
    val adjustedCPLossBlack : Double,
    val longestBestOrExcellentStreakRed : Int,
    val longestBestOrExcellentStreakBlack : Int,
    val blunderRateRed : Double,
    val blunderRateBlack : Double,
    val averageBlunderInterarrivalTimeRed : Double,
    val averageBlunderInterarrivalTimeBlack : Double,
    //TODO: come back to what the time series features will be
    val accuracyRed : Double,
    val accuracyBlack : Double,
    val gameLength : Int,
    val averageThinkTimeRed : Double,
    val averageThinkTimeBlack : Double,
    val stdevThinkTimeRed : Double,
    val stdevThinkTimeBlack : Double
){
    init {
        require(gameTimer > 0)
        require(moveTimer > 0)
        require(increment >= 0)
        require(usernameSimilarity > 0.0)
        require(longestBestOrExcellentStreakRed >= 0)
        require(longestBestOrExcellentStreakBlack >= 0)
        require(blunderRateRed >= 0.0)
        require(blunderRateBlack >= 0.0)
        require(averageBlunderInterarrivalTimeRed >= 0.0)
        require(averageBlunderInterarrivalTimeBlack >= 0.0)
        require(accuracyRed in 0.00..1.00)
        require(accuracyBlack in 0.00..1.00)
        require(gameLength > 0)
        require(averageThinkTimeRed >= 0.0)
        require(averageThinkTimeBlack >= 0.0)
        require(stdevThinkTimeRed >= 0.0)
        require(stdevThinkTimeBlack >= 0.0)
    }
}
