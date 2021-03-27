import java.util.*;

public class Graph {
    Map<Integer, Node> nodes;
    Map<Integer, Edge> edges;
    private int lastNodeId = 0;
    private int lastEdgeId = 0;

    public Graph() {
        nodes = new HashMap<>();
        var t = new Node( 320, 240);
        t.text = "Welcome to Grapher!\nLoad existing graphs from the File menu\nManipulate graphs with the toolbar on the left";
        addNode(t);
        addNode(150, 150);
        edges = new HashMap<>();
        addEdge(0, 1);
    }
    public int addNode(double x, double y) {
        return addNode(new Node(x, y));
    }
    public int addNode(Node node) {
        nodes.put(lastNodeId++,node);
        return lastNodeId;
    }
    public void removeNode(int id) {
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
