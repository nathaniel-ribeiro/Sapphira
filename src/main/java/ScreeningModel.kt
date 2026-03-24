import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import smile.anomaly.IsolationForest
import smile.data.DataFrame
import smile.feature.imputation.SimpleImputer

val mapper: ObjectMapper = jacksonObjectMapper().apply {
    factory.setStreamReadConstraints(
        StreamReadConstraints.builder()
            .maxStringLength(Int.MAX_VALUE)
            .build()
    )
    registerModule(com.fasterxml.jackson.module.paramnames.ParameterNamesModule())
    enable(SerializationFeature.INDENT_OUTPUT)
}

class ScreeningModel(
    @JsonSerialize(using = SmileBase64Serializer::class)
    @JsonDeserialize(using = SmileBase64Deserializer::class)
    val imputer : SimpleImputer? = null,
    @JsonSerialize(using = SmileBase64Serializer::class)
    @JsonDeserialize(using = SmileBase64Deserializer::class)
    val forest : IsolationForest? = null,
    val isFitted : Boolean = false
) {
    fun fit(data: Array<DoubleArray>) : ScreeningModel{
        val numCols = data[0].size
        val df = DataFrame.of(data, *(0 until numCols).map { "feature_$it" }.toTypedArray())
        val fittedImputer = SimpleImputer.fit(df)
        val imputedData = fittedImputer.apply(df).toArray()
        val computedSubsamplingRate = (TARGET_NUM_TRAINING_SAMPLES_PER_TREE / data.size.toDouble()).coerceIn(MIN_SAMPLING_RATE..MAX_SAMPLING_RATE)
        val options = IsolationForest.Options(N_TREES, MAX_DEPTH, computedSubsamplingRate, EXTENSION_LEVEL)
        val fittedForest = IsolationForest.fit(imputedData, options)
        return ScreeningModel(fittedImputer, fittedForest, true)
    }

    fun predict(data : Array<DoubleArray>) : DoubleArray{
        requireNotNull(imputer){"Must call fit() before predict(). Are you sure you are using a fitted ScreeningModel?"}
        requireNotNull(forest){"Must call fit() before predict(). Are you sure you are using a fitted ScreeningModel?"}
        require(isFitted){"Must call fit() before predict(). Are you sure you are using a fitted ScreeningModel?"}
        val numCols = data[0].size
        val df = DataFrame.of(data, *(0 until numCols).map { "feature_$it" }.toTypedArray())
        val imputedData = imputer.apply(df).toArray()
        return forest.score(imputedData)
    }

    fun toJson() : String = mapper.writeValueAsString(this)

    companion object{
        fun fromJson(json : String) : ScreeningModel = mapper.readValue(json)
        const val N_TREES = 100
        const val MAX_DEPTH = Int.MAX_VALUE
        const val TARGET_NUM_TRAINING_SAMPLES_PER_TREE = 256
        const val EXTENSION_LEVEL = 0
        const val MIN_SAMPLING_RATE = 1e-6
        const val MAX_SAMPLING_RATE = 1.0 - MIN_SAMPLING_RATE
    }
}