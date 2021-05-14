package grapher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Wraps graph object.
 */
public class GraphWrapper implements IGraph{
    /**
     * The graph being manipulated.
     */
    Graph graph = new Graph();
    /**
     * The path to the currently worked on file.
     */
    @Nullable File graphPath = null;

    @Override
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

    @Override
    public boolean save(File file) throws IOException {
        graphPath = file;
        return save();
    }

    @Override
    public boolean load(@Nullable File file) throws IOException {
        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            graph = mapper.readValue(file, Graph.class);
            graphPath = file;
            Logger.info("Loaded file from: "+file);
            return true;
        }
        return false;
    }

    @Override
    public void load(@NotNull URL src) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        graph = mapper.readValue(src, Graph.class);
        graphPath = new File(src.getFile());
        Logger.info("Loaded file from: "+src);
    }

    @Override
    public void loadDefault() {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            load(classLoader.getResource("default.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphPath = null;
    }

    @Override
    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(graph.nodes);
    }

    @Override
    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(graph.edges);
    }

    @Override
    public void addNode(double x, double y) {
        final var node = graph.addNode(x,y);
        Logger.info("Added Node (" + node + ")");
    }

    @Override
    public void addEdge(Node from, Node to) throws InvalidOperationException {
        if (from == to) {
            Logger.warn("Tried to connect Node with itself");
            return;
        }
        if (from == null || to == null)
        {
            Logger.warn("Tried to connect from or to null Node");
            throw new InvalidOperationException();
        }
        else{
            var edge = graph.addEdge(from, to);
            Logger.info("Added Edge (" + edge + ")");
        }
    }

    @Override
    public void removeNode(Node node){
        Logger.info("Removed Node (" + node + ")");
        graph.removeNode(node);
    }

    @Override
    public void setNodeTranslate(Node node, double x, double y) {
        node.setXY(x, y);
    }


    @Override
    public void removeEdge(Edge edge) {
        Logger.info("Removed Edge (" + edge + ")");
        graph.removeEdge(edge);
    }

    @Override
    public void setNodeText(Node node, String text) {
        node.text = text;
    }

    @Override
    public void setEdgeText(Edge edge, String text) {
        edge.text = text;
    }

    @Override
    public void setNodeShape(Node node, eNodeShape shape) {
        node.shape = shape;
    }

    @Override
    public void reset() {
        graph = new Graph();
        graphPath = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphWrapper that = (GraphWrapper) o;
        return Objects.equals(graph, that.graph) && Objects.equals(graphPath, that.graphPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph, graphPath);
    }
}
