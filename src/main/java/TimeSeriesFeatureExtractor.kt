import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.pow

class TimeSeriesFeatureExtractor : IFeatureProvider {
    override fun extract(reviewedGame: ReviewedGame): Map<String, Double?> {
        TODO("Not yet implemented")
    }
    // features from:
    // https://www.geeksforgeeks.org/data-analysis/advanced-feature-extraction-and-selection-from-time-series-data-using-tsfresh-in-python/

    fun varianceLargerThanStdev(signal: List<Double>) : Boolean {
        val stdev = StandardDeviation().evaluate(signal.toDoubleArray())
        val variance = stdev.pow(2)
        return variance > stdev
    }

    fun hasDuplicateMin(signal: List<Double>) : Boolean {
        val min = signal.min()
        val occurrencesOfMin = signal.count { it == min }
        return occurrencesOfMin > 1
    }

    fun hasDuplicateMax(signal: List<Double>) : Boolean {
        val max = signal.max()
        val occurrencesOfMax = signal.count { it == max }
        return occurrencesOfMax > 1
    }

    fun hasDuplicate(signal: List<Double>) : Boolean {
        return signal.distinct().size == signal.size
    }

    fun absEnergy(signal: List<Double>) : Double {
        return signal.sumOf { abs(it) }
    }

    fun meanAbsChange(signal: List<Double>) : Double {
        return signal.zipWithNext().map { (cur, next) -> abs(next - cur) }.average()
    }

    fun meanChange(signal : List<Double>) : Double {
        return signal.zipWithNext().map { (cur, next) -> next - cur }.average()
    }

    fun meanSecondDerivativeCentral(signal: List<Double>) : Double {
        return signal.asSequence()
                     .zipWithNext()
                     .map {
                         (cur, next) -> next - cur
                     }
                     .zipWithNext()
                     .map { (cur, next) -> next - cur }
                     .average()
    }

    fun median(signal: List<Double>) : Double {
        val descriptiveStatistics = DescriptiveStatistics()
        signal.forEach { descriptiveStatistics.addValue(it) }
        return descriptiveStatistics.getPercentile(50.0)
    }

    fun fourierEntropy(signal: List<Double>, bins: Int) : Double {
        TODO()
    }

    fun permutationEntropy(signal: List<Double>, dimension: Int, tau: Int) : Double {
        TODO()
    }

    fun meanOfAbsMaxima(signal: List<Double>, numberOfMaxima: Int) : Double {
        val absSignal = signal.map { abs(it) }
        val pq = PriorityQueue<Double>()
        absSignal.forEach {
            if(pq.size < numberOfMaxima){
                pq.add(it)
                return@forEach
            }
            val root = pq.peek()
            if(root >= it) return@forEach
            pq.poll()
            pq.add(it)
        }
        return pq.average()
    }
}