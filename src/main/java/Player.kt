data class Player(
    val username: String,
    val isGuest: Boolean,
    val isBanned: Boolean,
    val rating: Int
){
    init{
        require(rating >= 0)
    }
}
