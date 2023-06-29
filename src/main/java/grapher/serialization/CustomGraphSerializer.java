package grapher.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import grapher.model.Graph;
import grapher.model.settings.EEdgePlace;
import grapher.model.settings.Settings;

import java.io.IOException;

/**
 * Serializes graph using custom settings.
 */
public class CustomGraphSerializer extends StdSerializer<Graph> {
    private final Settings settings;

    public CustomGraphSerializer(Settings settings) {
        super((Class<Graph>) null);
        this.settings = settings;
    }

    @Override
    public void serialize(Graph value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (settings.collapseGraphIntoNodes) {
            gen.writeObject(value.nodes);
            return;
        }

        gen.writeStartObject();
        if (settings.writeGraphName)
            gen.writeStringField("name", value.name);
        gen.writeObjectField("nodes", value.nodes);
        if (settings.eEdgePlace == EEdgePlace.SEPARATE) {
            gen.writeObjectField("edge", value.edges);
        }
        gen.writeEndObject();
    }
}
