class Features(
    private val _game : Game,
    val usernameSimilarity : Double,
    val adjustedCPLossRed : Double?,
    val adjustedCPLossBlack : Double?,
    val longestBestOrExcellentStreakRed : Int,
    val longestBestOrExcellentStreakBlack : Int,
    val blunderRateRed : Double,
    val blunderRateBlack : Double,
    val averageBlunderInterarrivalTimeRed : Double,
    val averageBlunderInterarrivalTimeBlack : Double,
    val accuracyRed : Double,
    val accuracyBlack : Double,
    val medianThinkTimeRed : Double,
    val medianThinkTimeBlack : Double,
    val iqrThinkTimeRed : Double,
    val iqrThinkTimeBlack : Double
){
    val gameTimer = _game.gameTimer
    val moveTimer = _game.moveTimer
    val increment = _game.increment
    val ratingRed = _game.redPlayer.rating
    val ratingBlack = _game.blackPlayer.rating
    val isGuestRed = _game.redPlayer.isGuest
    val isGuestBlack = _game.blackPlayer.isGuest
    val resultRed = _game.resultRed
    val resultBlack = _game.resultBlack
    val gameResultReason = _game.gameResultReason

    init {
        require(usernameSimilarity > 0.0)
        require(longestBestOrExcellentStreakRed >= 0)
        require(longestBestOrExcellentStreakBlack >= 0)
        require(blunderRateRed >= 0.0)
        require(blunderRateBlack >= 0.0)
        require(averageBlunderInterarrivalTimeRed >= 0.0)
        require(averageBlunderInterarrivalTimeBlack >= 0.0)
        require(accuracyRed in 0.00..1.00)
        require(accuracyBlack in 0.00..1.00)
        require(medianThinkTimeRed >= 0.0)
        require(medianThinkTimeBlack >= 0.0)
        require(iqrThinkTimeRed >= 0.0)
        require(iqrThinkTimeBlack >= 0.0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Features) return false
        return toNumerical() == other.toNumerical()
    }

    override fun hashCode(): Int {
        return toNumerical().hashCode()
    }

    fun toNumerical() : List<Double?> {
        val isGuestRedNumerical = if(isGuestRed) 1.0 else 0.0
        val isGuestBlackNumerical = if(isGuestBlack) 1.0 else 0.0
        val resultRedOneHot = GameResult.entries.map { if(it == resultRed) 1.0 else 0.0 }
        val resultBlackOneHot = GameResult.entries.map { if(it == resultBlack) 1.0 else 0.0 }
        val gameResultReasonOneHot = GameResultReason.entries.map { if(it == gameResultReason) 1.0 else 0.0 }

        val numericalFeatures = buildList {
            add(gameTimer.toDouble())
            add(moveTimer.toDouble())
            add(increment.toDouble())
            add(ratingRed.toDouble())
            add(ratingBlack.toDouble())
            add(isGuestRedNumerical)
            add(isGuestBlackNumerical)
            addAll(resultRedOneHot)
            addAll(resultBlackOneHot)
            addAll(gameResultReasonOneHot)
            add(usernameSimilarity)
            add(adjustedCPLossRed)
            add(adjustedCPLossBlack)
            add(longestBestOrExcellentStreakRed.toDouble())
            add(longestBestOrExcellentStreakBlack.toDouble())
            add(blunderRateRed)
            add(blunderRateBlack)
            add(averageBlunderInterarrivalTimeRed)
            add(averageBlunderInterarrivalTimeBlack)
            add(accuracyRed)
            add(accuracyBlack)
            add(medianThinkTimeRed)
            add(medianThinkTimeBlack)
            add(iqrThinkTimeRed)
            add(iqrThinkTimeBlack)
        }

        return numericalFeatures
    }

}
