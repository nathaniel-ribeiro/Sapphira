import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import smile.anomaly.IsolationForest
import smile.data.DataFrame
import smile.data.transform.InvertibleColumnTransform
import smile.feature.imputation.KNNImputer
import smile.feature.transform.Standardizer
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.min

val mapper: ObjectMapper = jacksonObjectMapper().apply {
    factory.setStreamReadConstraints(
        StreamReadConstraints.builder()
            .maxStringLength(Int.MAX_VALUE)
            .build()
    )
    registerModule(com.fasterxml.jackson.module.paramnames.ParameterNamesModule())
    enable(SerializationFeature.INDENT_OUTPUT)
}

class ScreeningModel private constructor(
    @JsonSerialize(using = SmileBase64Serializer::class)
    @JsonDeserialize(using = SmileBase64Deserializer::class)
    val scaler : InvertibleColumnTransform,
    val imputer : KNNImputer,
    @JsonSerialize(using = SmileBase64Serializer::class)
    @JsonDeserialize(using = SmileBase64Deserializer::class)
    val forest : IsolationForest,
) {
    fun predict(data : Array<DoubleArray>) : DoubleArray{
        val df = DataFrame.of(data)
        val scaledDf = scaler.apply(df)
        val imputedDf = imputer.apply(scaledDf).toArray()
        return forest.score(imputedDf)
    }

    fun toJson() : String = mapper.writeValueAsString(this)

    companion object{
        const val N_TREES = 100
        const val TARGET_NUM_TRAINING_SAMPLES_PER_TREE = 256
        const val EXTENSION_LEVEL = 0
        const val MIN_SAMPLING_RATE = 1e-6
        const val MAX_SAMPLING_RATE = 1.0 - MIN_SAMPLING_RATE

        fun fit(data: Array<DoubleArray>) : ScreeningModel{
            val df = DataFrame.of(data)
            val scaler = Standardizer.fit(df)
            val scaledDf = scaler.apply(df)
            val imputer = KNNImputer(scaledDf, 5)
            val imputedData = imputer.apply(scaledDf).toArray()
            val computedSubsamplingRate = (TARGET_NUM_TRAINING_SAMPLES_PER_TREE / data.size.toDouble()).coerceIn(MIN_SAMPLING_RATE..MAX_SAMPLING_RATE)
            val maxDepth = ceil(log2(min(TARGET_NUM_TRAINING_SAMPLES_PER_TREE, data.size).toDouble())).toInt()
            val options = IsolationForest.Options(N_TREES, maxDepth, computedSubsamplingRate, EXTENSION_LEVEL)
            val forest = IsolationForest.fit(imputedData, options)
            return ScreeningModel(scaler, imputer, forest)
        }

        fun fromJson(json : String) : ScreeningModel = mapper.readValue(json)
    }
}