package grapher.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import grapher.model.Node;
import grapher.model.settings.Settings;

import java.io.IOException;

public class CustomNodeSerializer extends StdSerializer<Node> {
    private final Settings settings;

    public CustomNodeSerializer(Settings settings) {
        super((Class<Node>) null);
        this.settings = settings;
    }

    @Override
    public void serialize(Node value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.id);
        if (settings.writeNodeX)
            gen.writeNumberField("x", value.x);
        if (settings.writeNodeY)
            gen.writeNumberField("y", value.y);
        if (settings.writeNodeShape)
            gen.writeStringField("shape", value.shape.name());
        gen.writeStringField("text", value.text);
        gen.writeEndObject();
    }
}
