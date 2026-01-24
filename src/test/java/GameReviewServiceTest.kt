import org.junit.jupiter.api.*
import io.mockk.*

internal class GameReviewServiceTest {
    private val mockfish : Pikafish = mockk<Pikafish>()
    private val game : Game = mockk<Game>()
    @Test
    fun test1(){
        // boards for startpos and boards resulting from 1. h2e2 h9g7 2. h0g2
        val firstPly = Move("h2", "e2", Alliance.RED)
        val secondPly = Move("h9", "g7", Alliance.BLACK)
        val thirdPly =  Move("h0", "g2", Alliance.RED)
        val fourthPly = Move("i9", "h9", Alliance.BLACK)
        val fifthPly = Move("i0", "h0", Alliance.RED)
        val sixthPly = Move("b9", "c7", Alliance.BLACK)
        val seventhPly = Move("b0", "c2", Alliance.RED)

        val zeroethBoard = Board.STARTING_BOARD
        val firstBoard = Board("rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C4/9/RNBAKABNR b - - 0 1")
        val secondBoard = Board("rnbakab1r/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C4/9/RNBAKABNR w - - 2 2")
        val thirdBoard = Board("rnbakab1r/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C1N2/9/RNBAKAB1R b - - 3 2")
        val fourthBoard = Board("rnbakabr1/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C1N2/9/RNBAKAB1R w - - 0 1")
        val fifthBoard = Board("rnbakabr1/9/1c4nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C1N2/9/RNBAKABR1 b - - 0 1")
        val sixthBoard = Board("r1bakabr1/9/1cn3nc1/p1p1p1p1p/9/9/P1P1P1P1P/1C2C1N2/9/RNBAKABR1 w - - 0 1")
        val seventhBoard = Board("r1bakabr1/9/1cn3nc1/p1p1p1p1p/9/9/P1P1P1P1P/1CN1C1N2/9/R1BAKABR1 b - - 0 1")

        every { mockfish.makeMove(zeroethBoard, firstPly) } returns firstBoard
        every { mockfish.makeMove(firstBoard, secondPly) } returns secondBoard
        every { mockfish.makeMove(secondBoard, thirdPly) } returns thirdBoard
        every { mockfish.makeMove(thirdBoard, fourthPly) } returns fourthBoard
        every { mockfish.makeMove(fourthBoard, fifthPly) } returns fifthBoard
        every { mockfish.makeMove(fifthBoard, sixthPly) } returns sixthBoard
        every { mockfish.makeMove(sixthBoard, seventhPly) } returns seventhBoard

        every { mockfish.evaluate(zeroethBoard) } returns Evaluation(36, 0.095, 0.897, 0.008)
        every { mockfish.evaluate(firstBoard) } returns Evaluation(-28, 0.011, 0.915, 0.074)
        every { mockfish.evaluate(secondBoard) } returns Evaluation(34, 0.090, 0.901, 0.009)
        every { mockfish.evaluate(thirdBoard) } returns Evaluation(-31, 0.010,0.910 ,0.080)
        every { mockfish.evaluate(fourthBoard) } returns Evaluation(29, 0.077,0.912, 0.011)
        every { mockfish.evaluate(fifthBoard) } returns Evaluation(-23, 0.013, 0.925, 0.062)
        every { mockfish.evaluate(sixthBoard) } returns Evaluation(40, 0.107, 0.886, 0.007)
        every { mockfish.evaluate(seventhBoard) } returns Evaluation(-24, 0.013, 0.923, 0.064)

        every { game.moves } returns listOf(firstPly, secondPly, thirdPly, fourthPly, fifthPly, sixthPly, seventhPly)

        val gameReviewService = GameReviewService(mockfish)
        val reviewedGame = gameReviewService.review(game)
        println(reviewedGame)
    }
}