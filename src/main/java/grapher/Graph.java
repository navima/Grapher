package grapher;// CHECKSTYLE:OFF

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Data structure for a graph.
 */
@JsonDeserialize(using = GraphDeserializer.class)
public class Graph{
    /**
     * The nodes of the graph.
     */
    public final @NotNull Set<Node> nodes;
    /**
     * The edges of the graph.
     */
    public final @NotNull Set<Edge> edges;
    /**
     * The largest conflicting Node id.
     */
    private int lastNodeId = 0;
    /**
     * The largest conflicting Edge id.
     */
    private int lastEdgeId = 0;

    /**
     * Default constructor.
     */
    public Graph() {
        nodes = new HashSet<>();
        edges = new HashSet<>();
    }

    /**
     * Cosntructor / Builder used for serialization.
     * @param nodes      {@link Graph#nodes}
     * @param edges      {@link Graph#edges}
     * @param lastNodeId {@link Graph#lastNodeId}
     * @param lastEdgeId {@link Graph#lastEdgeId}
     */
    public Graph(@NotNull Set<Node> nodes, @NotNull Set<Edge> edges, int lastNodeId, int lastEdgeId) {
        this.nodes = nodes;
        this.edges = edges;
        this.lastNodeId = lastNodeId;
        this.lastEdgeId = lastEdgeId;
    }

    /**
     * Add a new node at specified location.
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
     * @param node The node to add.
     */
    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Remove a node from the graph.
     * @param node The node to remove.
     */
    public void removeNode(Node node) {
        nodes.remove(node);
        var edges = (HashSet<Edge>) node.edges.clone();
        for (var edge : edges)
            removeEdge(edge);
    }

    /**
     * Add a new edge connecting the two nodes.
     * @param from The start node.
     * @param to The end node.
     * @return The constructed edge.
     */
    public Edge addEdge(Node from, Node to) {
        var edge = new Edge(lastEdgeId++, from, to);
        addEdge(edge);
        return edge;
    }

    /**
     * Add an existing edge to the graph.
     * @param edge The edge to add.
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
        edge.from.edges.add(edge);
        edge.to.edges.add(edge);
    }

    /**
     * Remove an edge from the graph.
     * @param edge The edge to remove.
     */
    public void removeEdge(Edge edge) {
        edges.remove(edge);
        edge.from.edges.remove(edge);
        edge.to.edges.remove(edge);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return lastNodeId == graph.lastNodeId && lastEdgeId == graph.lastEdgeId && Objects.equals(nodes, graph.nodes) && Objects.equals(edges, graph.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, edges, lastNodeId, lastEdgeId);
    }

    @Override
    public @NotNull String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", lastNodeId=" + lastNodeId +
                ", lastEdgeId=" + lastEdgeId +
                '}';
    }
}
