import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.abs

class EvaluationTest {
    @Test
    fun starting_position_evaluation_test(){
        val evaluation = Evaluation(38, Alliance.RED)
        val expectedWinPercent = 51.0448478711
        assert(isClose(evaluation.winPercent, expectedWinPercent))
    }

    @Test
    fun dead_lost_evaluation_test() {
        val evaluation = Evaluation(-9_999, Alliance.RED)
        val expectedCentipawns = -2_000
        val expectedWinPercent = 9.97504891197
        assertEquals(expectedCentipawns, evaluation.centipawns)
        assert(isClose(expectedWinPercent, evaluation.winPercent))
    }

    @Test
    fun dead_won_evaluation_test() {
        val evaluation = Evaluation(9_999, Alliance.RED)
        val expectedCentipawns = 2_000
        val expectedWinPercent = 90.024951088
        assertEquals(expectedCentipawns, evaluation.centipawns)
        assert(isClose(expectedWinPercent, evaluation.winPercent))
    }

    @Test
    fun dead_draw_evaluation_test() {
        val evaluation = Evaluation(0, Alliance.RED)
        val expectedWinPercent = 50.0
        assert(isClose(expectedWinPercent, evaluation.winPercent))
    }

    @Test
    fun equivalent_evaluation_test() {
        val evaluation1 = Evaluation(2_000, Alliance.RED)
        val evaluation2 = Evaluation(9_999, Alliance.RED)
        assert(evaluation1 == evaluation2)
    }

    @Test
    fun comparison_evaluation_test() {
        val evaluation1 = Evaluation()
    }

    private fun isClose(a : Double, b : Double) : Boolean {
        return abs(a - b) <= 0.001
    }
}