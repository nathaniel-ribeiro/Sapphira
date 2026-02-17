import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import smile.anomaly.IsolationForest
import smile.data.DataFrame
import smile.feature.imputation.SimpleImputer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

val mapper: ObjectMapper = jacksonObjectMapper().apply {
    registerModule(com.fasterxml.jackson.module.paramnames.ParameterNamesModule())
    enable(SerializationFeature.INDENT_OUTPUT)
}

class SmileBase64Serializer : JsonSerializer<Any>() {
    override fun serialize(value: Any, gen: JsonGenerator, serializers: SerializerProvider) {
        val baos = ByteArrayOutputStream()
        ObjectOutputStream(baos).use { it.writeObject(value) }
        gen.writeString(Base64.getEncoder().encodeToString(baos.toByteArray()))
    }
}

class SmileBase64Deserializer : JsonDeserializer<Any>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Any {
        val bytes = Base64.getDecoder().decode(p.text)
        return ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() }
    }
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
        val fittedForest = IsolationForest.fit(imputedData)
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
    }
}