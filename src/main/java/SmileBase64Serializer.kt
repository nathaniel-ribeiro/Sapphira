import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.util.Base64

class SmileBase64Serializer : JsonSerializer<Any>() {
    override fun serialize(value: Any, gen: JsonGenerator, serializers: SerializerProvider) {
        val baos = ByteArrayOutputStream()
        ObjectOutputStream(baos).use { it.writeObject(value) }
        gen.writeString(Base64.getEncoder().encodeToString(baos.toByteArray()))
    }
}