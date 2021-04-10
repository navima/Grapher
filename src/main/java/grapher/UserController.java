package grapher;

import org.tinylog.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * A wrapper/abstraction of a {@link Graph} object, with saving functionality, extra checks and logging.
 */
public class UserController {
    /**
     * The graph being manipulated.
     */
    Graph graph = new Graph();
    /**
     * The path to the currently worked on file.
     */
    File graphPath = null;


    /**
     * Constructs a grapher.UserController.
     * Should be same as {@link UserController#reset()}
     */
    public UserController() {}

    /**
     * Constructs a grapher.UserController.
     * @param loadDefault Whether to load <code>default.json</code> welcome graph.
     */
    public UserController(boolean loadDefault){
        this();
        if (loadDefault){
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                load(classLoader.getResource("default.json"));
                graphPath = null; // hack to avoid accidentally overwriting default.json (also to achieve expected behaviour)
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves graph if path is set.
     * @return Whether path is set
     * @throws IOException On IOException
     */
    public boolean save() throws IOException {
        if (graphPath == null)
            return false;
        else{
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(graphPath, graph);
            Logger.info("Overwritten save at: "+graphPath);
            return true;
        }
    }

    /**
     * Saves graph to JSON.
     * @param file The source File
     * @return Always True
     * @throws IOException On IOException
     */
    public boolean save(File file) throws IOException {
        graphPath = file;
        return save();
    }

    /**
     * Loads graph JSON.
     * @param file The source File
     * @return Whether File was null.
     * @throws IOException On IOException
     */
    public boolean load(File file) throws IOException {
        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            graph = mapper.readValue(file, Graph.class);
            graphPath = file;
            Logger.info("Loaded file from: "+file);
            return true;
        }
        return false;
    }

    /**
     * Loads graph JSON.
     * @param src The source URL
     * @throws IOException On IOException
     */
    public void load(URL src) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        graph = mapper.readValue(src, Graph.class);
        graphPath = new File(src.getFile());
        Logger.info("Loaded file from: "+src);
    }

    /**
     * Adds a grapher.Node to graph.
     * @param x {@link Node#x}
     * @param y {@link Node#y}
     */
    public void addNode(double x, double y) {
        final var gotId = graph.addNode(x,y) - 1;
        Logger.info("Added Node #" + gotId + " (" + graph.getNode(gotId) + ")");
    }

    /**
     * Thrown when an invalid operation has been requested. (eg. adding Edge to nonexistent Node).
     */
    public static class InvalidOperationException extends Exception {}

    /**
     * Adds grapher.Edge to graph.
     * @param from grapher.Edge source
     * @param to grapher.Edge destination
     * @throws InvalidOperationException On invalid Node IDs
     */
    public void addEdge(int from, int to) throws InvalidOperationException {
        if (from == to) {
            Logger.warn("Tried to connect Node with itself");
            return;
        }
        if (graph.getNode(from) == null || graph.getNode(to) == null)
        {
            Logger.warn("Tried to connect from or to null Node (id not found)");
            throw new InvalidOperationException();
        }
        else{
            final var gotId = graph.addEdge(from, to) - 1;
            Logger.info("Added grapher.Edge #" + gotId + " (" + graph.getEdge(gotId) + ")");
        }
    }

    /**
     * Removes grapher.Node from graph.
     * @param id ID of grapher.Node
     */
    public void removeNode(int id) {
        Logger.info("Removed grapher.Node #" + id + " (" + graph.getNode(id) + ")");
        graph.removeNode(id);
    }

    /**
     * Sets the location of a grapher.Node.
     * @param id ID of grapher.Node
     * @param x New X
     * @param y New Y
     */
    public void setNodeTranslate(int id, double x, double y) {
        if (graph.nodes.containsKey(id))
            graph.getNode(id).setXY(x, y);
    }

    /**
     * Removes edge from graph.
     * @param id ID of grapher.Edge
     */
    public void removeEdge(int id) {
        Logger.info("Removed Edge #" + id + " (" + graph.getEdge(id) + ")");
        graph.removeEdge(id);
    }
    /**
     * Sets the text of a grapher.Node.
     * @param id ID of grapher.Node
     * @param text New text
     */
    public void setNodeText(int id, String text) {
        var t = graph.getNode(id);
        if (t != null)
            t.text = text;
    }

    /**
     * Sets the text of an grapher.Edge.
     * @param id ID of grapher.Edge
     * @param text New text
     */
    public void setEdgeText(int id, String text) {
        var t = graph.getEdge(id);
        if (t != null)
            t.text = text;
    }
    /**
     * Sets the shape of a grapher.Node.
     * @param id ID of grapher.Node
     * @param shape New shape
     */
    public void setNodeShape(int id, eNodeShape shape) {
        var t = graph.getNode(id);
        if (t != null)
            t.shape = shape;
    }
    /**
     *  Resets controller to default state. (blank graph, no path).
     */
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
        return "grapher.UserController{" +
                "graph=" + graph +
                ", graphPath=" + graphPath +
                '}';
    }
}
