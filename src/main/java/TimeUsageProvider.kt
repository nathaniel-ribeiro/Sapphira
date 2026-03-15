import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.rank.Median

class TimeUsageProvider : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame, alliance : Alliance): Map<String, Double?> {
        val reviewedMovesForAlliance = reviewedGame.reviewedMoves.filter { it.movePlayed.whoMoved == alliance }
        if (reviewedMovesForAlliance.any { it.movePlayed.thinkTime == null }){
            return mapOf(
                "Think Time Median" to null,
                "Think Time IQR" to null,
                "Think Time Outlier Fraction" to null,
                "Move Number of Longest Think" to null
            )
        }
        @Suppress("UNCHECKED_CAST")
        val thinkTimesNonNull = reviewedMovesForAlliance.map { it.movePlayed.thinkTime } as List<Int>
        @Suppress("UNCHECKED_CAST")
        val moveAccuraciesWithThinkTimes = reviewedMovesForAlliance.map { Pair(it.moveAccuracy, it.movePlayed.thinkTime) } as List<Pair<Double, Int>>

        val median = getMedian(thinkTimesNonNull)
        val interquartileRange = getInterQuartileRange(thinkTimesNonNull)
        val outlierFraction = getOutlierFraction(thinkTimesNonNull)
        val moveNumberOfLongestThink = getMoveNumberOfLongestThink(thinkTimesNonNull)
        val accuracyOfLongestThink = getAccuracyOfLongestThink(moveAccuraciesWithThinkTimes)

        return mapOf(
            "Think Time Median" to median,
            "Think Time IQR" to interquartileRange,
            "Think Time Outlier Fraction" to outlierFraction,
            "Move Number of Longest Think" to moveNumberOfLongestThink.toDouble(),
            "Accuracy of Longest Think" to accuracyOfLongestThink
        )
    }

    private fun getMedian(thinkTimes : List<Int>) : Double {
        val median = Median()
        val valuesWithoutNulls = thinkTimes.map { it.toDouble() }.toDoubleArray()
        return median.evaluate(valuesWithoutNulls)
    }

    private fun getInterQuartileRange(thinkTimes : List<Int>) : Double {
        val values = thinkTimes.map { it.toDouble() }.toDoubleArray()
        val descriptiveStatistics = DescriptiveStatistics(values)
        val q3 = descriptiveStatistics.getPercentile(75.0)
        val q1 = descriptiveStatistics.getPercentile(25.0)
        val iqr = q3 - q1
        return iqr
    }

    private fun getOutlierFraction(thinkTimes : List<Int>) : Double {
        val values = thinkTimes.map { it.toDouble() }.toDoubleArray()
        val descriptiveStatistics = DescriptiveStatistics(values)
        val q3 = descriptiveStatistics.getPercentile(75.0)
        val q1 = descriptiveStatistics.getPercentile(25.0)
        val iqr = q3 - q1
        val tukeyFenceLowerBound = q1 - 1.5 * iqr
        val tukeyFenceUpperBound = q3 + 1.5 * iqr
        val numOutliers = values.count { it !in tukeyFenceLowerBound..tukeyFenceUpperBound }
        return numOutliers / values.size.toDouble()
    }

    private fun getAccuracyOfLongestThink(moveAccuraciesWithThinkTimes : List<Pair<Double, Int>>) : Double {
        return moveAccuraciesWithThinkTimes.maxBy { it.second }.first
    }

    private fun getMoveNumberOfLongestThink(thinkTimes : List<Int>) : Int {
        return thinkTimes.indices.maxBy { thinkTimes[it] } + 1
    }
}