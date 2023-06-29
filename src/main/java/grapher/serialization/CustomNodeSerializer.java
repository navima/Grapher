package grapher.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import grapher.model.Graph;
import grapher.model.Node;
import grapher.model.settings.EEdgePlace;
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
        var graph = (Graph) ((ContextWriter) gen.getOutputTarget()).getContext();
        gen.writeStartObject();
        gen.writeNumberField(settings.nodeIdName, value.id);
        if (settings.writeNodeX)
            gen.writeNumberField("x", value.x);
        if (settings.writeNodeY)
            gen.writeNumberField("y", value.y);
        if (settings.writeNodeShape)
            gen.writeStringField("shape", value.shape.name());
        if (settings.eEdgePlace == EEdgePlace.EMBEDDED_IN_FROM_NODE) {
            gen.writeObjectField(
                    settings.fromNodeEdgeFieldName,
                    graph.edges.stream().filter(edge -> edge.from.id == value.id).toList());
        }
        gen.writeStringField(settings.nodeTextName, value.text);
        gen.writeEndObject();
    }
}
