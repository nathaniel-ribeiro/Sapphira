class Evaluation(val centipawns : Int,
                 val winProbability : Double,
                 val drawProbability : Double,
                 val loseProbability : Double){
    init {
        require(winProbability >= 0)
        require(drawProbability >= 0)
        require(loseProbability >= 0)
        require(winProbability + drawProbability + loseProbability == 1.0){"Win/draw/loss values must form a probability distribution (sum to 1 and be non-negative)"}
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Evaluation) return false
        return this.winProbability == other.winProbability &&
                this.drawProbability == other.drawProbability &&
                this.loseProbability == other.loseProbability
    }

    override fun hashCode(): Int {
        var result = this.centipawns.hashCode()
        result = result * 31 + this.winProbability.hashCode()
        result = result * 31 + this.drawProbability.hashCode()
        result = result * 31 + this.loseProbability.hashCode()
        return result
    }

    fun flip() : Evaluation{
        return Evaluation(-this.centipawns, this.loseProbability, this.drawProbability, this.winProbability)
    }

    fun toPawns() : Double{
        return this.centipawns / 100.0
    }

    override fun toString(): String {
        return "Evaluation(centipawns=$centipawns, winProbability=$winProbability, drawProbability=$drawProbability, loseProbability=$loseProbability)"
    }
}