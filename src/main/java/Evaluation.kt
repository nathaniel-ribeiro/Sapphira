import kotlin.math.abs
import kotlin.math.exp

class Evaluation(val centipawns : Int,
                 val winProbability : Double,
                 val drawProbability : Double,
                 val loseProbability : Double,
                 val perspective : Alliance) : Comparable<Evaluation>{

    val expectedScore = winProbability + 0.5 * drawProbability
    // formula copied from https://lichess.org/page/accuracy
    val winPercent = 50 + 50 * (2 / (1 + exp(-0.00368208 * centipawns)) - 1)
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
                this.loseProbability == other.loseProbability &&
                this.centipawns == other.centipawns &&
                this.perspective == other.perspective
    }

    override fun hashCode(): Int {
        var result = this.centipawns.hashCode()
        result = result * 31 + winProbability.hashCode()
        result = result * 31 + drawProbability.hashCode()
        result = result * 31 + loseProbability.hashCode()
        result = result * 31 + perspective.hashCode()
        return result
    }

    fun flip() : Evaluation{
        return Evaluation(-1 * centipawns, loseProbability, drawProbability, winProbability, perspective.flip())
    }

    fun toPawns() : Double{
        return this.centipawns / 100.0
    }

    override fun toString(): String {
        return "Evaluation(centipawns=$centipawns, winProbability=$winProbability, drawProbability=$drawProbability, loseProbability=$loseProbability, perspective=$perspective)"
    }

    override fun compareTo(other: Evaluation): Int {
        return centipawns.compareTo(other.centipawns)
    }

    companion object{
        val RED_LOST = Evaluation(-10_000, 0.0, 0.0, 1.0, Alliance.RED)
        val RED_DRAW = Evaluation(0, 0.0, 1.0, 0.0, Alliance.RED)
        val RED_WON = Evaluation(10_000, 1.0, 0.0, 0.0, Alliance.RED)
        val BLACK_LOST = RED_WON.flip()
        val BLACK_DRAW = RED_DRAW.flip()
        val BLACK_WON = RED_LOST.flip()
    }
}