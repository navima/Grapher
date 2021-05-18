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
    public final @NotNull Set<Node> nodes;
    public final @NotNull Set<Edge> edges;
    private int lastNodeId = 0;
    private int lastEdgeId = 0;

    public Graph() {
        nodes = new HashSet<>();
        edges = new HashSet<>();
    }

    public Graph(@NotNull Set<Node> nodes, @NotNull Set<Edge> edges, int lastNodeId, int lastEdgeId) {
        this.nodes = nodes;
        this.edges = edges;
        this.lastNodeId = lastNodeId;
        this.lastEdgeId = lastEdgeId;
    }

    public Node addNode(double x, double y) {
        var node = new Node(x, y, lastNodeId++);
        addNode(node);
        return node;
    }
    public void addNode(Node node) {
        nodes.add(node);
    }
    public void removeNode(Node node) {
        nodes.remove(node);
        var edges = (HashSet<Edge>) node.edges.clone();
        for (var edge : edges)
            removeEdge(edge);
    }
    public Edge addEdge(Node from, Node to) {
        var edge = new Edge(lastEdgeId++, from, to);
        addEdge(edge);
        return edge;
    }
    public void addEdge(Edge edge) {
        edges.add(edge);
        edge.from.edges.add(edge);
        edge.to.edges.add(edge);
    }
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
