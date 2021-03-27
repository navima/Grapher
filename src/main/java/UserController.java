import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UserController {
    Graph graph = new Graph();
    File graphPath;

    public UserController() {
        //load(new File(getClass().getResource("default.json").getFile()));
        load(getClass().getResource("default.json")) ;

    }

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
        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                graph = mapper.readValue(file, Graph.class);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
    void load(URL src) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            graph = mapper.readValue(src, Graph.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void reset() {
        graph = new Graph();
        graphPath = null;
    }
}
