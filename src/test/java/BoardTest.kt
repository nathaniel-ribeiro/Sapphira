import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BoardTest {
    @Test
    fun starting_board_test(){
        val fen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1"
        val board = Board(fen)
        assertEquals(Alliance.RED, board.whoseTurn)
        assertEquals(1, board.fullMoveNumber)
        assertEquals(0, board.pliesSinceACapture)
    }
}