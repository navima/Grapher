import java.util.*;

public class Graph {
    public Map<Integer, Node> nodes;
    public Map<Integer, Edge> edges;
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
    public void removeNode(int id) {
        for (var iterator = edges.entrySet().iterator(); iterator.hasNext();){
            var next = iterator.next().getValue();
            if (next.to == id || next.from == id) {
                iterator.remove();
            }
        }
        nodes.remove(id);
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
}
