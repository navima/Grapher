package grapher.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import grapher.model.Edge;
import grapher.model.settings.Settings;
import grapher.util.Utils;

import java.io.IOException;

/**
 * Serializes edge using IDs to avoid cyclic reference.
 */
public class CustomEdgeSerializer extends StdSerializer<Edge> {
    private final Settings settings;

    public CustomEdgeSerializer(Settings settings) {
        super((Class<Edge>) null);
        this.settings = settings;
    }

    @Override
    public void serialize(Edge value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        if (settings.writeEdgeId)
            gen.writeNumberField("id", value.id);
        if (settings.writeEdgeFrom)
            gen.writeNumberField("from", value.from.id);
        if (settings.writeEdgeTo)
            gen.writeNumberField(settings.edgeToName, value.to.id);
        gen.writeStringField(settings.edgeTextName, Utils.transformNullIf(value.text, settings.transformNull));
        if (settings.writeEdgePoints)
            gen.writeObjectField("points", value.points);
        gen.writeEndObject();
    }
}
