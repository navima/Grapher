package grapher.model;// CHECKSTYLE:OFF

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import grapher.memento.GraphMemento;
import grapher.memento.IMementoable;
import grapher.serialization.GraphDeserializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data structure for a graph.
 */
@JsonDeserialize(using = GraphDeserializer.class)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Graph implements IMementoable<Graph> {
    public Set<Node> nodes = new HashSet<>();
    public Set<Edge> edges = new HashSet<>();
    private int lastNodeId;
    private int lastEdgeId;

    /**
     * Copy constructor.
     */
    public Graph(Graph oldGraph) {
        for (var oldNode : oldGraph.nodes)
            addNode(Node.copyWithoutEdges(oldNode));
        for (var oldEdge : oldGraph.edges) {
            var fromNode = nodes.stream().filter(node -> oldEdge.from.id == node.id).findFirst().get();
            var toNode = nodes.stream().filter(node -> oldEdge.to.id == node.id).findFirst().get();
            var edge = new Edge(oldEdge.id, fromNode, toNode, oldEdge.text, new ArrayList<>(oldEdge.points));
            addEdge(edge);
        }
        lastNodeId = oldGraph.lastNodeId;
        lastEdgeId = oldGraph.lastEdgeId;
    }

    /**
     * Add a new node at specified location.
     *
     * @param x {@link Node#x}
     * @param y {@link Node#y}
     * @return Reference to the constructed node
     */
    public Node addNode(double x, double y) {
        var node = new Node(x, y, lastNodeId++);
        addNode(node);
        return node;
    }

    /**
     * Add an existing node to the graph.
     *
     * @param node The node to add.
     */
    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Remove a node from the graph.
     *
     * @param node The node to remove.
     */
    public void removeNode(Node node) {
        nodes.remove(node);
        var edges = new HashSet<>(node.edges);
        for (var edge : edges)
            removeEdge(edge);
    }

    /**
     * Add a new edge connecting the two nodes.
     *
     * @param from The start node.
     * @param to   The end node.
     * @return The constructed edge.
     */
    public Edge addEdge(Node from, Node to) {
        var edge = new Edge(lastEdgeId++, from, to);
        addEdge(edge);
        return edge;
    }

    /**
     * Add an existing edge to the graph.
     *
     * @param edge The edge to add.
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
        edge.from.edges.add(edge);
        edge.to.edges.add(edge);
    }

    /**
     * Remove an edge from the graph.
     *
     * @param edge The edge to remove.
     */
    public void removeEdge(Edge edge) {
        edges.remove(edge);
        edge.from.edges.remove(edge);
        edge.to.edges.remove(edge);
    }

    @JsonIgnore
    @Override
    public GraphMemento getState() {
        return new GraphMemento(this);
    }

    /**
     * Removes n-th point from an edge.
     */
    public void removeEdgeNode(Edge edge, int n) {
        edge.points.remove(n);
    }
}
