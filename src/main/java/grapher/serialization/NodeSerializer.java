package grapher.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import grapher.model.Node;

import java.io.IOException;

/**
 * Class for serializing {@link Node}.
 */
public class NodeSerializer extends StdSerializer<Node> {
    public NodeSerializer() {
        this(null);
    }

    protected NodeSerializer(Class<Node> t) {
        super(t);
    }

    @Override
    public void serialize(Node value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.id);
        gen.writeNumberField("x", value.x);
        gen.writeNumberField("y", value.y);
        gen.writeStringField("shape", value.shape.name());
        gen.writeStringField("text", value.text);
        gen.writeEndObject();
    }
}
