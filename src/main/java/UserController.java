import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.shape.Shape;

import java.io.File;
import java.io.IOException;

public class UserController {
    Graph graph = new Graph();
    File graphPath;

    public UserController() {}

    boolean save() {
        if (graphPath == null)
            return false;
        else{
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writeValue(graphPath, graph);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    boolean save(File file) {
        graphPath = file;
        return save();
    }
    boolean load(File file) {
        //TODO
        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                var temp = mapper.readValue(file, Graph.class);
                graph = temp;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
    void addNode(double x, double y) {
        final var gotId = graph.addNode(x,y) - 1;
        System.out.println("Added Node #" + gotId + " (" + graph.getNode(gotId) + ")");
    }
    void addEdge(int from, int to) {
        if (from == to)
            return;
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

    public void setNodeShape(int id, eNodeShape shape) {
        graph.getNode(id).shape = shape;
    }
}
