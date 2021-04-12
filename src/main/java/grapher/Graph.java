package grapher;// CHECKSTYLE:OFF

import java.util.*;

public class Graph {
    public final Map<Integer, Node> nodes;
    public final Map<Integer, Edge> edges;
    private int lastNodeId = 0;
    private int lastEdgeId = 0;

    public int getLastNodeId() {
        return lastNodeId;
    }
    public int getLastEdgeId() {
        return lastEdgeId;
    }

    public Graph() {
        nodes = new HashMap<>();
        edges = new HashMap<>();
    }

    public int addNode(double x, double y) {
        return addNode(new Node(x, y));
    }
    public int addNode(Node node) {
        nodes.put(lastNodeId++,node);
        return lastNodeId;
    }
    public Node removeNode(int id) {
        for (var iterator = edges.entrySet().iterator(); iterator.hasNext();){
            var next = iterator.next().getValue();
            if (next.to == id || next.from == id) {
                iterator.remove();
            }
        }
        return nodes.remove(id);
    }
    public Node getNode(int id) {
        return nodes.get(id);
    }

    public int addEdge(int from, int to) {
        return addEdge(new Edge(from, to));
    }
    public int addEdge(Edge edge) {
        edges.put(lastEdgeId++,edge);
        return lastEdgeId;
    }
    public void removeEdge(int id) {
        edges.remove(id);
    }
    public Edge getEdge(int id) {
        return edges.get(id);
    }

    @Override
    public boolean equals(Object o) {
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
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", lastNodeId=" + lastNodeId +
                ", lastEdgeId=" + lastEdgeId +
                '}';
    }
}
