import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.abs

class EvaluationTest {
    @Test
    fun compareEvaluationsEqualWinProbabilityTest(){
        val eval1 = Evaluation(700, 1.0, 0.0, 0.0, Alliance.RED)
        val eval2 = Evaluation(500, 1.0, 0.0, 0.0, Alliance.RED)
        assertTrue { eval1 > eval2 }
    }

    @Test
    fun compareEvaluationsEqualLoseProbabilityTest(){
        val eval1 = Evaluation(-500, 0.0, 0.0, 1.0, Alliance.RED)
        val eval2 = Evaluation(-700, 0.0, 0.0, 1.0, Alliance.RED)
        assertTrue { eval1 > eval2 }
    }

    @Test
    fun flipEvaluationTest(){
        val eval = Evaluation(38, 0.108, 0.885, 0.007, Alliance.RED)
        val expectedFlippedEval = Evaluation(-38, 0.007, 0.885, 0.108, Alliance.BLACK)
        assertEquals(expectedFlippedEval, eval.flip())
    }

    @Test
    fun winPercentTest(){
        val eval = Evaluation(38, 0.108, 0.885, 0.007, Alliance.RED)
        val expectedWinPercent = 53.4922803954
        assertTrue(abs(expectedWinPercent - eval.winPercent) <= 0.001)
    }
}