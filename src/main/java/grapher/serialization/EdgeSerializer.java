package grapher.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import grapher.model.Edge;

import java.io.IOException;

/**
 * Serializes edge using IDs to avoid cyclic reference.
 */
public class EdgeSerializer extends StdSerializer<Edge> {
    public EdgeSerializer() {
        this(null);
    }

    protected EdgeSerializer(Class<Edge> t) {
        super(t);
    }

    @Override
    public void serialize(Edge value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.id);
        gen.writeNumberField("from", value.from.id);
        gen.writeNumberField("to", value.to.id);
        gen.writeStringField("text", value.text);
        gen.writeObjectField("points", value.points);
        gen.writeEndObject();
    }
}
