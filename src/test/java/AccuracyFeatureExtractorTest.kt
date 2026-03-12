import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import Alliance.*

class AccuracyFeatureExtractorTest {
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
        every { reviewedMove1R.movePlayed } returns Move("b2", "e2", RED)
        every { reviewedMove1R.movePlayedEvaluation } returns Evaluation(centipawns=31, perspective= RED)
        every { reviewedMove1R.bestMoveEvaluation } returns Evaluation(centipawns=46, perspective= RED)

        // 1. ... b7e7
        every { reviewedMove1B.movePlayed } returns Move("b7", "e7", BLACK)
        every { reviewedMove1B.movePlayedEvaluation } returns Evaluation(centipawns=-61, perspective= BLACK)
        every { reviewedMove1B.bestMoveEvaluation } returns Evaluation(centipawns=-31, perspective= BLACK)

        // 2. b0c2
        every { reviewedMove2R.movePlayed } returns Move("b0", "c2", RED)
        every { reviewedMove2R.movePlayedEvaluation } returns Evaluation(centipawns=62, perspective= RED)
        every { reviewedMove2R.bestMoveEvaluation } returns Evaluation(centipawns=61, perspective=RED)

        // 2. ... b9c7
        every { reviewedMove2B.movePlayed } returns Move("b9", "c7", BLACK)
        every { reviewedMove2B.movePlayedEvaluation } returns Evaluation(centipawns=-65, perspective=BLACK)
        every { reviewedMove2B.bestMoveEvaluation } returns Evaluation(centipawns=-62, perspective=BLACK)

        // 3. c3c4
        every { reviewedMove3R.movePlayed } returns Move("c3", "c4", RED)
        every { reviewedMove3R.movePlayedEvaluation } returns Evaluation(centipawns=19, perspective=RED)
        every { reviewedMove3R.bestMoveEvaluation } returns Evaluation(centipawns=65, perspective=RED)

        // 3. ... a9b9
        every { reviewedMove3B.movePlayed } returns Move("a9", "b9", BLACK)
        every { reviewedMove3B.movePlayedEvaluation } returns Evaluation(centipawns=-27, perspective=BLACK)
        every { reviewedMove3B.bestMoveEvaluation } returns Evaluation(centipawns=-19, perspective=BLACK)

        // 4. h0g2
        every { reviewedMove4R.movePlayed } returns Move("h0", "g2", RED)
        every { reviewedMove4R.movePlayedEvaluation } returns Evaluation(centipawns=27, perspective=RED)
        every { reviewedMove4R.bestMoveEvaluation } returns Evaluation(centipawns=27, perspective=RED)

        // 4. ... g6g5
        every { reviewedMove4B.movePlayed } returns Move("g6", "g5", BLACK)
        every { reviewedMove4B.movePlayedEvaluation } returns Evaluation(centipawns=-35, perspective=BLACK)
        every { reviewedMove4B.bestMoveEvaluation } returns Evaluation(centipawns=-27, perspective=BLACK)

        // 5. h2h6
        every { reviewedMove5R.movePlayed } returns Move("h2", "h6", RED)
        every { reviewedMove5R.movePlayedEvaluation } returns Evaluation(centipawns=30, perspective=RED)
        every { reviewedMove5R.bestMoveEvaluation } returns Evaluation(centipawns=35, perspective=RED)

        // 5. ... h9g7
        every { reviewedMove5B.movePlayed } returns Move("h9", "g7", BLACK)
        every { reviewedMove5B.movePlayedEvaluation } returns Evaluation(centipawns=-40, perspective=BLACK)
        every { reviewedMove5B.bestMoveEvaluation } returns Evaluation(centipawns=-30, perspective=BLACK)

        // 6. a0a1
        every { reviewedMove6R.movePlayed } returns Move("a0", "a1", RED)
        every { reviewedMove6R.movePlayedEvaluation } returns Evaluation(centipawns=37, perspective=RED)
        every { reviewedMove6R.bestMoveEvaluation } returns Evaluation(centipawns=40, perspective=RED)

        // 6. ... h7i7
        every { reviewedMove6B.movePlayed } returns Move("h7", "i7", BLACK)
        every { reviewedMove6B.movePlayedEvaluation } returns Evaluation(centipawns=-114, perspective=BLACK)
        every { reviewedMove6B.bestMoveEvaluation } returns Evaluation(centipawns=-37, perspective=BLACK)

        // 7. i0h0
        every { reviewedMove7R.movePlayed } returns Move("i0", "h0", RED)
        every { reviewedMove7R.movePlayedEvaluation } returns Evaluation(centipawns=104, perspective=RED)
        every { reviewedMove7R.bestMoveEvaluation } returns Evaluation(centipawns=114, perspective=RED)

        // 7. ... i9h9
        every { reviewedMove7B.movePlayed } returns Move("i9", "h9", BLACK)
        every { reviewedMove7B.movePlayedEvaluation } returns Evaluation(centipawns=-112, perspective=BLACK)
        every { reviewedMove7B.bestMoveEvaluation } returns Evaluation(centipawns=-104, perspective=BLACK)

        // 8. a1f1
        every { reviewedMove8R.movePlayed } returns Move("a1", "f1", RED)
        every { reviewedMove8R.movePlayedEvaluation } returns Evaluation(centipawns=91, perspective=RED)
        every { reviewedMove8R.bestMoveEvaluation } returns Evaluation(centipawns=112, perspective=RED)

        // 8. ... b9b3
        every { reviewedMove8B.movePlayed } returns Move("b9", "b3", BLACK)
        every { reviewedMove8B.movePlayedEvaluation } returns Evaluation(centipawns=-99, perspective=BLACK)
        every { reviewedMove8B.bestMoveEvaluation } returns Evaluation(centipawns=-91, perspective=BLACK)

        // 9. h6g6
        every { reviewedMove9R.movePlayed } returns Move("h6", "g6", RED)
        every { reviewedMove9R.movePlayedEvaluation } returns Evaluation(centipawns=52, perspective=RED)
        every { reviewedMove9R.bestMoveEvaluation } returns Evaluation(centipawns=99, perspective=RED)

        // 9. ... g7e8
        every { reviewedMove9B.movePlayed } returns Move("g7", "e8", BLACK)
        every { reviewedMove9B.movePlayedEvaluation } returns Evaluation(centipawns=-1009, perspective=BLACK)
        every { reviewedMove9B.bestMoveEvaluation } returns Evaluation(centipawns=-52, perspective=BLACK)

        // 10. e2e6
        every { reviewedMove10R.movePlayed } returns Move("e2", "e6", RED)
        every { reviewedMove10R.movePlayedEvaluation } returns Evaluation(centipawns=-481, perspective=RED)
        every { reviewedMove10R.bestMoveEvaluation } returns Evaluation(centipawns=1009, perspective=RED)

        // 10. ... h9h0
        every { reviewedMove10B.movePlayed } returns Move("h9", "h0", BLACK)
        every { reviewedMove10B.movePlayedEvaluation } returns Evaluation(centipawns=489, perspective=BLACK)
        every { reviewedMove10B.bestMoveEvaluation } returns Evaluation(centipawns=481, perspective=BLACK)

        // 11. e6e5
        every { reviewedMove11R.movePlayed } returns Move("e6", "e5", RED)
        every { reviewedMove11R.movePlayedEvaluation } returns Evaluation(centipawns=-568, perspective=RED)
        every { reviewedMove11R.bestMoveEvaluation } returns Evaluation(centipawns=-489, perspective=RED)

        // 11. ... h0h6
        every { reviewedMove11B.movePlayed } returns Move("h0", "h6", BLACK)
        every { reviewedMove11B.movePlayedEvaluation } returns Evaluation(centipawns=514, perspective=BLACK)
        every { reviewedMove11B.bestMoveEvaluation } returns Evaluation(centipawns=568, perspective=BLACK)

        // 12. f0e1
        every { reviewedMove12R.movePlayed } returns Move("f0", "e1", RED)
        every { reviewedMove12R.movePlayedEvaluation } returns Evaluation(centipawns=-686, perspective=RED)
        every { reviewedMove12R.bestMoveEvaluation } returns Evaluation(centipawns=-514, perspective=RED)

        // 12. ... h6g6
        every { reviewedMove12B.movePlayed } returns Move("h6", "g6", BLACK)
        every { reviewedMove12B.movePlayedEvaluation } returns Evaluation(centipawns=677, perspective=BLACK)
        every { reviewedMove12B.bestMoveEvaluation } returns Evaluation(centipawns=686, perspective=BLACK)

        // 13. c2d4
        every { reviewedMove13R.movePlayed } returns Move("c2", "d4", RED)
        every { reviewedMove13R.movePlayedEvaluation } returns Evaluation(centipawns=-716, perspective=RED)
        every { reviewedMove13R.bestMoveEvaluation } returns Evaluation(centipawns=-677, perspective=RED)

        // 13. ... c7e6
        every { reviewedMove13B.movePlayed } returns Move("c7", "e6", BLACK)
        every { reviewedMove13B.movePlayedEvaluation } returns Evaluation(centipawns=707, perspective=BLACK)
        every { reviewedMove13B.bestMoveEvaluation } returns Evaluation(centipawns=716, perspective=BLACK)

        // 14. e0f0
        every { reviewedMove14R.movePlayed } returns Move("e0", "f0", RED)
        every { reviewedMove14R.movePlayedEvaluation } returns Evaluation(centipawns=-759, perspective=RED)
        every { reviewedMove14R.bestMoveEvaluation } returns Evaluation(centipawns=-707, perspective=RED)

        // 14. ... e7e5
        every { reviewedMove14B.movePlayed } returns Move("e7", "e5", BLACK)
        every { reviewedMove14B.movePlayedEvaluation } returns Evaluation(centipawns=-9999, perspective=BLACK)
        every { reviewedMove14B.bestMoveEvaluation } returns Evaluation(centipawns=759, perspective=BLACK)

        // 15. f1f9
        every { reviewedMove15R.movePlayed } returns Move("f1", "f9", RED)
        every { reviewedMove15R.movePlayedEvaluation } returns Evaluation(centipawns=10000, perspective=RED)
        every { reviewedMove15R.bestMoveEvaluation } returns Evaluation(centipawns=9999, perspective=RED)

        every { reviewedGame.reviewedMoves } returns listOf(reviewedMove1R, reviewedMove1B,
                                                            reviewedMove2R, reviewedMove2B,
                                                            reviewedMove3R, reviewedMove3B,
                                                            reviewedMove4R, reviewedMove4B,
                                                            reviewedMove5R, reviewedMove5B,
                                                            reviewedMove6R, reviewedMove6B,
                                                            reviewedMove7R, reviewedMove7B,
                                                            reviewedMove8R, reviewedMove8B,
                                                            reviewedMove9R, reviewedMove9B,
                                                            reviewedMove10R, reviewedMove10B,
                                                            reviewedMove11R, reviewedMove11B,
                                                            reviewedMove12R, reviewedMove12B,
                                                            reviewedMove13R, reviewedMove13B,
                                                            reviewedMove14R, reviewedMove14B,
                                                            reviewedMove15R)
    }
    @Test
    fun reviewedGame1Test(){
        val accuracyFeatureExtractor = AccuracyFeatureExtractor()
        val accuracies = accuracyFeatureExtractor.extract(reviewedGame)
        println(accuracies)
    }
}