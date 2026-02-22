enum class Alliance {
    RED {
        override fun flip(): Alliance {
            return BLACK
        }
    },
    BLACK {
        override fun flip(): Alliance {
            return RED
        }
    };
    abstract fun flip() : Alliance
}
