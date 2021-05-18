package grapher;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for serializing {@link Node}.
 */
public class NodeSerializer extends StdSerializer<Node> {

    /**
     * Default constructor.
     */
    public NodeSerializer(){
        this(null);
    }

    /**
     * Constructor.
     * @param t t
     */
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
