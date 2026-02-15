import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class Encoder {
    fun encode(features: Features): DoubleArray {
        val result = mutableListOf<Double>()
        val properties = Features::class.memberProperties.associateBy { it.name }
        val sortedProps = Features::class.primaryConstructor?.parameters?.map { properties[it.name]!! } ?: emptyList()
        for (prop in sortedProps) {
            when (val value = prop.get(features)) {
                is Double -> result.add(value)
                is Int -> result.add(value.toDouble())
                is Enum<*> -> {
                    val enumConstants = value::class.java.enumConstants
                    val index = value.ordinal
                    val oneHot = DoubleArray(enumConstants.size) { 0.0 }
                    oneHot[index] = 1.0
                    result.addAll(oneHot.toList())
                }
            }
        }
        return result.toDoubleArray()
    }
}