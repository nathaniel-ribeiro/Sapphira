import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import Alliance.*

class AccuracyCalculatorTest {
    private val game = Game(
        uuid = "example_uuid",
        redPlayer = Player(username = "example_red_player", isGuest = false, isBanned = false, rating = 500),
        blackPlayer = Player(username = "example_black_player", isGuest = false, isBanned = false, rating = 500),
        gameTimer = 10,
        moveTimer = 2,
        increment = 0,
        moves = listOf(
            // 1.
            Move("b2", "e2", RED),
            Move("b7", "e7", BLACK),
            //2.
            Move("b0", "c2", RED),
            Move("b9", "c7", BLACK),
            // 3.
            Move("c3", "c4", RED),
            Move("a9", "b9", BLACK),
            // 4.
            Move("h0", "g2", RED),
            Move("g6", "g5", BLACK),
            // 5.
            Move("h2", "h6", RED),
            Move("h9", "g7", BLACK),
            // 6.
            Move("a0", "a1", RED),
            Move("h7", "i7", BLACK),
            // 7.
            Move("i0", "h0", RED),
            Move("i9", "h9", BLACK),
            // 8.
            Move("a1", "f1", RED),
            Move("b9", "b3", BLACK),
            // 9.
            Move("h6", "g6", RED),
            Move("g7", "e8", BLACK),
            // 10.
            Move("e2", "e6", RED),
            Move("h9", "h0", BLACK),
            // 11.
            Move("e6", "e5", RED),
            Move("h0", "h6", BLACK),
            // 12.
            Move("f0", "e1", RED),
            Move("h6", "g6", BLACK),
            // 13.
            Move("c2", "d4", RED),
            Move("c7", "e6", BLACK),
            // 14.
            Move("e0", "f0", RED),
            Move("e7", "e5", BLACK),
            // 15.
            Move("f1", "f9", RED),
        ),
        resultRed = GameResult.WON,
        resultBlack = GameResult.LOST,
        gameResultReason = GameResultReason.CHECKMATE
    )

    private val reviewedGame = mockk<ReviewedGame>()
    private val reviewedMove1R = mockk<ReviewedMove>()
    private val reviewedMove1B = mockk<ReviewedMove>()
    private val reviewedMove2R = mockk<ReviewedMove>()
    private val reviewedMove2B = mockk<ReviewedMove>()
    private val reviewedMove3R = mockk<ReviewedMove>()
    private val reviewedMove3B = mockk<ReviewedMove>()
    private val reviewedMove4R = mockk<ReviewedMove>()
    private val reviewedMove4B = mockk<ReviewedMove>()
    private val reviewedMove5R = mockk<ReviewedMove>()
    private val reviewedMove5B = mockk<ReviewedMove>()
    private val reviewedMove6R = mockk<ReviewedMove>()
    private val reviewedMove6B = mockk<ReviewedMove>()
    private val reviewedMove7R = mockk<ReviewedMove>()
    private val reviewedMove7B = mockk<ReviewedMove>()
    private val reviewedMove8R = mockk<ReviewedMove>()
    private val reviewedMove8B = mockk<ReviewedMove>()
    private val reviewedMove9R = mockk<ReviewedMove>()
    private val reviewedMove9B = mockk<ReviewedMove>()
    private val reviewedMove10R = mockk<ReviewedMove>()
    private val reviewedMove10B = mockk<ReviewedMove>()
    private val reviewedMove11R = mockk<ReviewedMove>()
    private val reviewedMove11B = mockk<ReviewedMove>()
    private val reviewedMove12R = mockk<ReviewedMove>()
    private val reviewedMove12B = mockk<ReviewedMove>()
    private val reviewedMove13R = mockk<ReviewedMove>()
    private val reviewedMove13B = mockk<ReviewedMove>()
    private val reviewedMove14R = mockk<ReviewedMove>()
    private val reviewedMove14B = mockk<ReviewedMove>()
    private val reviewedMove15R = mockk<ReviewedMove>()

    @BeforeEach
    fun setup(){
        // 1. b2e2
        every { reviewedMove1R.movePlayedEvaluation } returns Evaluation(centipawns=31, winProbability=0.08, drawProbability=0.91, loseProbability=0.01, perspective= RED)
        every { reviewedMove1R.bestMoveEvaluation } returns Evaluation(centipawns=46, winProbability=0.132, drawProbability=0.862, loseProbability=0.006, perspective= RED)

        // 1. ... b7e7
        every { reviewedMove1B.movePlayedEvaluation } returns Evaluation(centipawns=-61, winProbability=0.004, drawProbability=0.795, loseProbability=0.201, perspective= BLACK)
        every { reviewedMove1B.bestMoveEvaluation } returns Evaluation(centipawns=-31, winProbability=0.01, drawProbability=0.91, loseProbability=0.08, perspective= BLACK)

        // 2. b0c2
        every { reviewedMove2R.movePlayedEvaluation } returns Evaluation(centipawns=62, winProbability=0.206, drawProbability=0.791, loseProbability=0.003, perspective= RED)
        every { reviewedMove2R.bestMoveEvaluation } returns Evaluation(centipawns=61, winProbability=0.201, drawProbability=0.795, loseProbability=0.004, perspective=RED)

        // 2. ... b9c7
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 3. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 3. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 4. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 4. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 5. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 5. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 6. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 6. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 7. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 7. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 8. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 8. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 9. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 9. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 10. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 10. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 11. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 11. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 12. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 12. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 13. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 13. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 14. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 14. ... c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

        //TODO
        // 15. c3c4
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, winProbability=0.003, drawProbability=0.774, loseProbability=0.223, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, winProbability=0.003, drawProbability=0.791, loseProbability=0.206, perspective=BLACK)

    }
    @Test
    fun reviewedGame1Test(){
        println(reviewedGame)
        val accuracyCalculator = AccuracyCalculator()
        val accuracies = accuracyCalculator.extract(reviewedGame)
        println(accuracies)
    }
}