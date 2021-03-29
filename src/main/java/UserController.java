import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class UserController {
    Graph graph = new Graph();
    File graphPath = null;

    public UserController() {}

    public UserController(boolean loadDefault){
        if (loadDefault){
            try {
                load(getClass().getResource("default.json")) ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean save() throws IOException {
        if (graphPath == null)
            return false;
        else{
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(graphPath, graph);
            return true;
        }
    }
    public boolean save(File file) throws IOException {
        graphPath = file;
        return save();
    }
    public boolean load(File file) throws IOException {
        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            graph = mapper.readValue(file, Graph.class);
            graphPath = file;
            return true;
        }
        return false;
    }
    public void load(URL src) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        graph = mapper.readValue(src, Graph.class);
        graphPath = new File(src.getFile());
    }
    public void addNode(double x, double y) {
        final var gotId = graph.addNode(x,y) - 1;
        System.out.println("Added Node #" + gotId + " (" + graph.getNode(gotId) + ")");
    }
    public static class InvalidOperationException extends Exception {}
    public void addEdge(int from, int to) throws InvalidOperationException {
        if (from == to)
            return;
        if (graph.getNode(from) == null || graph.getNode(to) == null)
            throw new InvalidOperationException();
        else{
            final var gotId = graph.addEdge(from, to) - 1;
            System.out.println("Added Edge #" + gotId + " (" + graph.getEdge(gotId) + ")");
        }
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
        var t = graph.getNode(id);
        if (t != null)
            t.text = text;
    }
    public void setEdgeText(int id, String text) {
        var t = graph.getEdge(id);
        if (t != null)
            t.text = text;
    }
    public void setNodeShape(int id, eNodeShape shape) {
        var t = graph.getNode(id);
        if (t != null)
            t.shape = shape;
    }
    public void reset() {
        graph = new Graph();
        graphPath = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserController that = (UserController) o;

        return Objects.equals(graph, that.graph) && Objects.equals(graphPath, that.graphPath);
    }

    @Override
    public int hashCode() {
        int result = graph != null ? graph.hashCode() : 0;
        result = 31 * result + (graphPath != null ? graphPath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserController{" +
                "graph=" + graph +
                ", graphPath=" + graphPath +
                '}';
    }
}
