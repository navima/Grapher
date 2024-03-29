package grapher.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import grapher.model.Edge;
import grapher.model.Graph;
import grapher.model.Node;
import grapher.shape.ENodeShape;
import javafx.geometry.Point2D;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;


/**
 * Class for deserializing Graph.
 */
public class GraphDeserializer extends StdDeserializer<Graph> {
    public GraphDeserializer() {
        this(null);
    }

    protected GraphDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Graph deserialize(JsonParser p, DeserializationContext ctxt) throws JsonProcessingException {
        try {
            final var root = p.getCodec().readTree(p);

            final var nodes = root.get("nodes");
            final var nodesSet = nodes.traverse(p.getCodec()).readValueAs(HashSet.class);
            final var parsedNodeSet = new HashSet<Node>();
            var lastNodeId = 0;
            for (var node : nodesSet) {
                LinkedHashMap<String, Object> n2 = (LinkedHashMap<String, Object>) node;
                final var id = (int) n2.get("id");
                final var x = (double) n2.get("x");
                final var y = (double) n2.get("y");
                final var text = (String) n2.get("text");
                final var shape = ENodeShape.valueOf((String) n2.get("shape"));
                final var nodeNode = new Node(x, y, id, shape, text);
                if (id > lastNodeId)
                    lastNodeId = id;
                parsedNodeSet.add(nodeNode);
            }
            lastNodeId++;

            final var edges = root.get("edges");
            final var edgesSet = edges.traverse(p.getCodec()).readValueAs(HashSet.class);
            final var parsedEdgeSet = new HashSet<Edge>();
            var lastEdgeId = 0;
            for (var edge : edgesSet) {
                LinkedHashMap<String, Object> e2 = (LinkedHashMap<String, Object>) edge;
                final var id = (int) e2.get("id");
                final var text = (String) e2.get("text");
                final var fromId = (int) e2.get("from");
                final var toId = (int) e2.get("to");
                final var from = parsedNodeSet.stream().filter(node -> node.id == fromId).findFirst().get();
                final var to = parsedNodeSet.stream().filter(node -> node.id == toId).findFirst().get();
                final var edgeEdge = new Edge(id, from, to, text, new ArrayList<>());
                if (e2.containsKey("points")) {
                    final var points = (ArrayList<LinkedHashMap<String, Object>>) e2.get("points");
                    final var parsedPoints = new ArrayList<Point2D>();
                    for (var elem : points)
                        parsedPoints.add(new Point2D((double) elem.get("x"), (double) elem.get("y")));
                    edgeEdge.points.addAll(parsedPoints);
                }
                if (id > lastEdgeId)
                    lastEdgeId = id;
                parsedEdgeSet.add(edgeEdge);
            }
            lastEdgeId++;

            var name = "";
            try {
                final var nameNode = (TextNode) root.get("name");
                name = nameNode.asText();
            } catch (Exception e) {
                Logger.warn("Graph missing 'name'");
            }
            var graph = new Graph(name, parsedNodeSet, new HashSet<>(), lastNodeId, lastEdgeId);
            for (var edge : parsedEdgeSet) {
                graph.addEdge(edge);
            }
            return graph;

        } catch (NullPointerException | IOException e) {
            throw JsonMappingException.from(p, "Exception thrown when trying to deserialize Graph object");
        }
    }
}
