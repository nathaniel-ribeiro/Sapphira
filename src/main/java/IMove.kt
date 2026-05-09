interface IMove {
    val srcSquare : String
    val destSquare : String
    val whoMoved : Alliance
    val thinkTime : Int?
}