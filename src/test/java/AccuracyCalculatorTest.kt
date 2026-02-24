import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AccuracyCalculatorTest {
    private val mockReviewedGame = mockk<ReviewedGame>()
    private val move1r =
    private val move1b;
    private val move2r;
    private val move2b;
    private val move3r;
    private val move3b;
    @Test
    fun reviewedGame1Test(){
        every { mockReviewedGame.reviewedMoves } returns listOf()

    }
}