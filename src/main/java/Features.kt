enum class Features : IFeature {
    ACCURACY {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    RATING {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    ADJUSTED_CENTIPAWN_LOSS {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    BLUNDER_RATE {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    LONGEST_STREAK_BEST_OR_EXCELLENT_PAST_OPENING {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    EVALUATION_AFTER_OPENING {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    THINK_TIME_MEDIAN {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    THINK_TIME_IQR {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    THINK_TIME_OUTLIER_FRACTION {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    ACCURACY_OF_LONGEST_THINK {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
    MOVE_NUMBER_OF_LONGEST_THINK {
        override fun extract(reviewedGame: ReviewedGame, alliance: Alliance): Double? {
            TODO("Not yet implemented")
        }
    },
}