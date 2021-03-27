import java.io.File;

public class UserController {
    Graph graph = new Graph();

    public UserController() {}

    boolean save() {
        //TODO
        return false;
    }
    boolean save(File file) {
        //TODO
        return false;
    }
    boolean load() {
        //TODO
        return false;
    }
    boolean load(File file) {
        //TODO
        return false;
    }
    void addNode(double x, double y) {
        final var gotId = graph.addNode(x,y) - 1;
        System.out.println("Added Node #" + gotId + " (" + graph.getNode(gotId) + ")");
    }
    void addEdge(int from, int to) {
        final var gotId = graph.addEdge(from, to)-1;
        System.out.println("Added Edge #" + gotId + " (" + graph.getEdge(gotId) + ")");
    }
    public void removeNode(int id) {
        System.out.println("Removed Node #" + id + " (" + graph.getNode(id) + ")");
        graph.removeNode(id);
    }
    public void setNodeTranslate(int id, double x, double y) {
        if (graph.nodes.containsKey(id))
            graph.getNode(id).setXY(x, y);}

    public void removeEdge(int id) {
        graph.removeEdge(id);
    }

    public void setNodeText(int id, String text) {
        graph.getNode(id).text = text;
    }

    public void setEdgeText(int id, String text) {
        graph.getEdge(id).text = text;
    }
}
