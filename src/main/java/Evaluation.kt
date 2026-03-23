import kotlin.math.exp

class Evaluation (private val _centipawnsRaw : Int,
                  val perspective : Alliance) : Comparable<Evaluation>{

    // formula from https://lichess.org/page/accuracy, modified for the inherent dynamism of Xiangqi
    val winPercent : Double
        get() {
            val rawWinPercent = 50 + 50 * (2 / (1 + exp(-0.0011 * centipawns)) - 1)
            return rawWinPercent.coerceIn(MIN_WIN_PERCENT..MAX_WIN_PERCENT)
        }

    val centipawns : Int get() = _centipawnsRaw.coerceIn(-1 * MAX_CENTIPAWNS..MAX_CENTIPAWNS)

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Evaluation) return false
        return this.centipawns == other.centipawns &&
                this.perspective == other.perspective
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

    override fun compareTo(other: Evaluation): Int {
        return centipawns.compareTo(other.centipawns)
    }

    companion object{
        val RED_LOST = Evaluation(-MAX_CENTIPAWNS, Alliance.RED)
        val RED_DRAW = Evaluation(0, Alliance.RED)
        val RED_WON = Evaluation(MAX_CENTIPAWNS, Alliance.RED)
        val BLACK_LOST = RED_WON.flip()
        val BLACK_DRAW = RED_DRAW.flip()
        val BLACK_WON = RED_LOST.flip()
        const val MAX_CENTIPAWNS = 2_000
        const val MIN_WIN_PERCENT = 9.97505
        const val MAX_WIN_PERCENT = 90.02495
    }
}