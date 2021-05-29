package grapher;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Class for serializing Edge.
 */
public class EdgeSerializer extends StdSerializer<Edge> {

    /**
     * Default constructor.
     */
    public EdgeSerializer(){
        this(null);
    }

    /**
     * Constructor.
     * @param t t
     */
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
