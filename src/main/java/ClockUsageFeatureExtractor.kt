import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.rank.Median

class ClockUsageFeatureExtractor : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame, alliance : Alliance): Map<String, Double?> {
        val thinkTimes = reviewedGame.reviewedMoves.map { it.movePlayed }.filter { it.whoMoved == alliance }.map { it.thinkTime }
        val thinkTimeMedian = median(thinkTimes)
        val thinkTimeIQR = iqr(thinkTimes)
        val thinkTimeOutlierFraction = outlierFraction(thinkTimes)

        return mapOf(
            "Think Time Median" to thinkTimeMedian,
            "Think Time IQR" to thinkTimeIQR,
            "Think Time Outlier Fraction" to thinkTimeOutlierFraction,
        )
    }

    private fun median(values : List<Int?>) : Double? {
        if(values.any { it == null }) return null
        val median = Median()
        val valuesWithoutNulls = values.filterNotNull().map { it.toDouble() }.toDoubleArray()
        return median.evaluate(valuesWithoutNulls)
    }

    private fun iqr(values : List<Int?>) : Double? {
        if(values.any { it == null }) return null
        val valuesWithoutNulls = values.filterNotNull().map { it.toDouble() }.toDoubleArray()
        val descriptiveStatistics = DescriptiveStatistics(valuesWithoutNulls)
        val q3 = descriptiveStatistics.getPercentile(75.0)
        val q1 = descriptiveStatistics.getPercentile(25.0)
        val iqr = q3 - q1
        return iqr
    }

    private fun outlierFraction(values : List<Int?>) : Double? {
        if(values.all { it == null }) return null
        val valuesWithoutNulls = values.filterNotNull().map { it.toDouble() }.toDoubleArray()
        val descriptiveStatistics = DescriptiveStatistics(valuesWithoutNulls)
        val q3 = descriptiveStatistics.getPercentile(75.0)
        val q1 = descriptiveStatistics.getPercentile(25.0)
        val iqr = q3 - q1
        val tukeyFenceLowerBound = q1 - 1.5 * iqr
        val tukeyFenceUpperBound = q3 + 1.5 * iqr
        val numOutliers = valuesWithoutNulls.count { it !in tukeyFenceLowerBound..tukeyFenceUpperBound }
        return numOutliers / valuesWithoutNulls.size.toDouble()
    }
}