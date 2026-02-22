import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

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
}