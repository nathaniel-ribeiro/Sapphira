import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.util.Base64

class SmileBase64Deserializer : JsonDeserializer<Any>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Any {
        val bytes = Base64.getDecoder().decode(p.text)
        return ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() }
    }
}