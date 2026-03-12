import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.rank.Median

class ClockUsageFeatureExtractor : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame): Map<String, Double?> {
        val redThinkTimes = reviewedGame.reviewedMoves.map { it.movePlayed }.filter { it.whoMoved == Alliance.RED }.map { it.thinkTime }
        val blackThinkTimes = reviewedGame.reviewedMoves.map { it.movePlayed }.filter { it.whoMoved == Alliance.BLACK }.map { it.thinkTime }
        val redThinkTimeMedian = median(redThinkTimes)
        val blackThinkTimeMedian = median(blackThinkTimes)
        val redThinkTimeIQR = iqr(redThinkTimes)
        val blackThinkTimeIQR = iqr(blackThinkTimes)

        return mapOf(
            "Red Think Time Median" to redThinkTimeMedian,
            "Black Think Time Median" to blackThinkTimeMedian,
            "Red Think Time IQR" to redThinkTimeIQR,
            "Black Think Time IQR" to blackThinkTimeIQR
        )
    }

    private fun median(values : List<Int?>) : Double? {
        if(values.all { it == null }) return null
        val median = Median()
        val valuesWithoutNulls = values.filterNotNull().map { it.toDouble() }.toDoubleArray()
        return median.evaluate(valuesWithoutNulls)
    }

    private fun iqr(values : List<Int?>) : Double? {
        if(values.all { it == null }) return null
        val valuesWithoutNulls = values.filterNotNull().map { it.toDouble() }.toDoubleArray()
        val descriptiveStatistics = DescriptiveStatistics(valuesWithoutNulls)
        val q3 = descriptiveStatistics.getPercentile(75.0)
        val q1 = descriptiveStatistics.getPercentile(25.0)
        val iqr = q3 - q1
        return iqr
    }
}