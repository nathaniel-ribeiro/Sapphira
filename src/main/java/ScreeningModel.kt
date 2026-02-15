import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import smile.anomaly.IsolationForest
import smile.data.DataFrame
import smile.feature.imputation.SimpleImputer

val mapper: ObjectMapper = jacksonObjectMapper().apply {
    setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
}

class ScreeningModel(val imputer : SimpleImputer? = null, val forest : IsolationForest? = null) {
    fun fit(data: Array<DoubleArray>) : ScreeningModel{
        val numCols = data[0].size
        val df = DataFrame.of(data, *(0 until numCols).map { "feature_$it" }.toTypedArray())
        val fittedImputer = SimpleImputer.fit(df)
        val imputedData = fittedImputer.apply(df).toArray()
        val fittedForest = IsolationForest.fit(imputedData)
        return ScreeningModel(fittedImputer, fittedForest)
    }

    fun predict(data : Array<DoubleArray>) : DoubleArray{
        requireNotNull(imputer){"Must call fit() before predict(). Are you sure you are using a fitted ScreeningModel?"}
        requireNotNull(forest){"Must call fit() before predict(). Are you sure you are using a fitted ScreeningModel?"}
        val numCols = data[0].size
        val df = DataFrame.of(data, *(0 until numCols).map { "feature_$it" }.toTypedArray())
        val imputedData = imputer.apply(df).toArray()
        return forest.score(imputedData)
    }

    fun toJson() : String = mapper.writeValueAsString(this)

    companion object{
        fun fromJson(json : String) : ScreeningModel = mapper.readValue(json)
    }
}