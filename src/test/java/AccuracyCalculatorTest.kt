import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

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
            Move("b2", "e2", Alliance.RED),
            Move("b7", "e7", Alliance.BLACK),
            //2.
            Move("b0", "c2", Alliance.RED),
            Move("b9", "c7", Alliance.BLACK),
            // 3.
            Move("c3", "c4", Alliance.RED),
            Move("a9", "b9", Alliance.BLACK),
            // 4.
            Move("h0", "g2", Alliance.RED),
            Move("g6", "g5", Alliance.BLACK),
            // 5.
            Move("h2", "h6", Alliance.RED),
            Move("h9", "g7", Alliance.BLACK),
            // 6.
            Move("a0", "a1", Alliance.RED),
            Move("h7", "i7", Alliance.BLACK),
            // 7.
            Move("i0", "h0", Alliance.RED),
            Move("i9", "h9", Alliance.BLACK),
            // 8.
            Move("a1", "f1", Alliance.RED),
            Move("b9", "b3", Alliance.BLACK),
            // 9.
            Move("h6", "g6", Alliance.RED),
            Move("g7", "e8", Alliance.BLACK),
            // 10.
            Move("e2", "e6", Alliance.RED),
            Move("h9", "h0", Alliance.BLACK),
            // 11.
            Move("e6", "e5", Alliance.RED),
            Move("h0", "h6", Alliance.BLACK),
            // 12.
            Move("f0", "e1", Alliance.RED),
            Move("h6", "g6", Alliance.BLACK),
            // 13.
            Move("c2", "d4", Alliance.RED),
            Move("c7", "e6", Alliance.BLACK),
            // 14.
            Move("e0", "f0", Alliance.RED),
            Move("e7", "e5", Alliance.BLACK),
            // 15.
            Move("f1", "f9", Alliance.RED),
        ),
        resultRed = GameResult.WON,
        resultBlack = GameResult.LOST,
        gameResultReason = GameResultReason.CHECKMATE
    )

    private val pikafish = Pikafish(File("/Volumes/External128/tempStorageContents/Kiwi Computing/Xiangqi/Pikafish/src/pikafish"), 15, 1024)
    private val gameReviewService = GameReviewService(pikafish)
    private val reviewedGame = gameReviewService.review(game, 1_000_000)

    @BeforeEach
    fun setup(){
        // TODO: set up mocked win percents and accuracy percents
    }
    @Test
    fun reviewedGame1Test(){
        println(reviewedGame.reviewedMoves.map { it.moveQuality })
        val accuracyCalculator = AccuracyCalculator()
        val accuracies = accuracyCalculator.extract(reviewedGame)
        println(accuracies)
    }
}