import kotlin.math.abs

class Evaluation(val centipawns : Int,
                 val winProbability : Double,
                 val drawProbability : Double,
                 val loseProbability : Double){

    val expectedScore = winProbability + 0.5 * drawProbability
    init {
        require(winProbability in 0.0..1.0)
        require(drawProbability in 0.0..1.0)
        require(loseProbability in 0.0..1.0)
        var totalProbabilityMass = winProbability + drawProbability + loseProbability
        require(abs(totalProbabilityMass - 1.0) <= 1e-6){"Win/draw/loss values must form a probability distribution (sum to 1 and be non-negative)"}
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

    companion object{
        val LOST = Evaluation(-10_000, 0.0, 0.0, 1.0)
        val DREW = Evaluation(0, 0.0, 1.0, 0.0)
        val WON = Evaluation(10_000, 1.0, 0.0, 0.0)
    }
}