package grapher;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * Class for deserializing Graph.
 */
public class GraphDeserializer extends StdDeserializer<Graph> {

    /**
     * Default constructor.
     */
    public GraphDeserializer(){
        this(null);
    }

    /**
     * Constructor.
     * @param vc vc
     */
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
            for (var node : nodesSet){
                LinkedHashMap<String, Object> n2 = (LinkedHashMap<String, Object>)node;
                final var id = (int) n2.get("id");
                final var x = (double) n2.get("x");
                final var y = (double) n2.get("y");
                final var text = (String) n2.get("text");
                final var shape = eNodeShape.valueOf((String)n2.get("shape"));
                final var nodeNode = new Node(x, y, id, shape, text);
                if(id > lastNodeId)
                    lastNodeId = id;
                parsedNodeSet.add(nodeNode);
            }
            lastNodeId++;

            final var edges = root.get("edges");
            final var edgesSet = edges.traverse(p.getCodec()).readValueAs(HashSet.class);
            final var parsedEdgeSet = new HashSet<Edge>();
            var lastEdgeId = 0;
            for(var edge : edgesSet){
                LinkedHashMap<String, Object> e2 = (LinkedHashMap<String, Object>)edge;
                final var id = (int) e2.get("id");
                final var text = (String) e2.get("text");
                final var fromId = (int) e2.get("from");
                final var toId = (int) e2.get("to");
                final var from = parsedNodeSet.stream().filter(node -> node.id == fromId).findFirst().get();
                final var to = parsedNodeSet.stream().filter(node -> node.id == toId).findFirst().get();
                final var edgeEdge = new Edge(id, from, to, text);
                if(e2.containsKey("points")){
                    final var points = (ArrayList<LinkedHashMap<String, Object>>) e2.get("points");
                    final var parsedPoints = new ArrayList<Point2D>();
                    for(var elem : points)
                        parsedPoints.add(new Point2D((double)elem.get("x"), (double)elem.get("y")));
                    edgeEdge.points.addAll(parsedPoints);
                }
                if(id > lastEdgeId)
                    lastEdgeId = id;
                parsedEdgeSet.add(edgeEdge);
            }

            var graph = new Graph(parsedNodeSet, new HashSet<>(), lastNodeId, lastEdgeId);
            for (var edge : parsedEdgeSet){
                graph.addEdge(edge);
            }
            return graph;

        } catch (NullPointerException | IOException e){
            throw JsonMappingException.from(p, "Exception thrown when trying to deserialize Graph object");
        }
    }
}
