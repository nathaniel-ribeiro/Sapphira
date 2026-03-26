import kotlin.math.exp

class Evaluation (private val _centipawnsRaw : Int, val perspective : Alliance){

    // formula from https://lichess.org/page/accuracy, modified for the inherent dynamism of Xiangqi
    val winPercent : Double get() = 50 + 50 * (2 / (1 + exp(-0.0011 * centipawns)) - 1)
    val centipawns : Int get() = _centipawnsRaw.coerceIn(-1 * MAX_CENTIPAWNS..MAX_CENTIPAWNS)

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Evaluation) return false
        val perspectiveCorrectedOtherEvaluation = if(other.perspective != this.perspective) other.flip() else other

        return this.centipawns == perspectiveCorrectedOtherEvaluation.centipawns &&
                this.perspective == perspectiveCorrectedOtherEvaluation.perspective
    }

    override fun hashCode(): Int {
        var result = this.centipawns.hashCode()
        result = result * 31 + perspective.hashCode()
        return result
    }

    fun flip() : Evaluation{
        return Evaluation(-1 * centipawns, perspective.flip())
    }

    override fun toString(): String {
        return "Evaluation(centipawns=$centipawns, perspective=$perspective)"
    }

    companion object{
        val RED_LOST = Evaluation(-MAX_CENTIPAWNS, Alliance.RED)
        val RED_WON = Evaluation(MAX_CENTIPAWNS, Alliance.RED)
        val BLACK_LOST = RED_WON.flip()
        val BLACK_WON = RED_LOST.flip()
        const val MAX_CENTIPAWNS = 2_000
    }
}