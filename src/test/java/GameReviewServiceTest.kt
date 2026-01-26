import org.junit.jupiter.api.*
import io.mockk.*

internal class GameReviewServiceTest {
    private val mockfish : Pikafish = mockk<Pikafish>()
    private val game : Game = mockk<Game>()
    @Test
    fun test1(){
        val movesString = listOf("h2f2", "h9g7", "g0e2", "c9e7", "g3g4", "c6c5", "h0g2", "h7i7", "g2f4", "b9c7", "i0g0", "i7i3", "b2b6", "c7b5", "b6g6",
                           "i9h9", "g6g5", "e7g5", "g4g5", "h9h4", "f4d5", "d9e8", "g5g6", "g7i8", "b0c2", "h4d4", "d5f6", "b7f7", "f2f7", "e8f7",
                           "a0a1", "a9d9", "d0e1", "f9e8", "a1b1", "b5c3", "b1b3", "c3e2", "c0e2", "i3b3", "g6g7", "d4f4", "f6h7", "b3b7", "h7g9",
                           "i8g9", "g7g8", "g9i8", "g8f8", "b7b8", "f8e8", "f7e8", "g0g6", "d9d6", "g6i6", "i8g7", "i6i9", "g7f9")
        val moves = movesString.mapIndexed { i, str ->
            Move(str.substring(0..1),
                str.substring(2..3),
                if(i.mod(2) == 0) Alliance.RED else Alliance.BLACK
            )
        }

        every { game.moves } returns moves

        val pikafish = Pikafish(ConfigOptions)
        val gameReviewService = GameReviewService(pikafish)
        val reviewedGame = gameReviewService.review(game)
        reviewedGame.reviewedMoves.forEachIndexed { i, reviewedMove ->
            if(reviewedMove.bestMoveEvaluation.centipawns < reviewedMove.movePlayedEvaluation.centipawns){
                println("Best move worse than move played for move $i : played = ${reviewedMove.movePlayed}, played cp = ${reviewedMove.movePlayedEvaluation.centipawns}, best cp = ${reviewedMove.bestMoveEvaluation.centipawns}")
            }
        }

        val extractor = FeatureExtractionService(ConfigOptions)
        println("${extractor.getAdjustedCPLoss(reviewedGame, Alliance.BLACK)}")
    }
}