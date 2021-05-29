package grapher;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wraps graph object.
 */
public class GraphWrapper implements IGraph {
    /**
     * The graph being manipulated.
     */
    Graph graph = new Graph();
    /**
     * The path to the currently worked on file.
     */
    @Nullable File graphPath = null;


    List<HistoryElement<GraphMemento>> history = new ArrayList<>();
    int historyPosition = 0;
    @Override
    public List<GraphMemento> getHistory() {
        return history.stream().map(HistoryElement::value).toList();
    }

    @Override
    public void restoreState(GraphMemento memento) {
        graph = new Graph(memento.getValue());
    }

    public void printHistory(){
        System.out.println("The current history:");
        for (int i = history.size()-1; i >= 0; i--){
            if (i == historyPosition)
                System.out.println("X " + history.get(i).label() + " : " + history.get(i).value());
            else
                System.out.println("- " + history.get(i).label() + " : " + history.get(i).value());
        }
    }

    @Override
    public void undo() {
        if (history.isEmpty() || historyPosition - 1 < 0)
            return;
        var prevState = history.get(--historyPosition);
        restoreState(prevState.value());
        //printHistory();
    }

    @Override
    public void redo(){
        if (history.isEmpty() || (historyPosition+1) > (history.size() - 1))
            return;
        var nextState = history.get(++historyPosition);
        restoreState(nextState.value());
        //printHistory();
    }

    @Override
    public void captureState(){
        captureState(null);
    }
    public void captureState(String label){
        if(history.size() - 1 > historyPosition){
            Logger.info("History is stale. Taking first "+(historyPosition+1)+" elements (from "+history.size()+")");
            history = history.stream().limit(historyPosition+1).collect(Collectors.toList());
        }
        history.add(new HistoryElement<>(label, graph.getState()));
        historyPosition=history.size()-1;
        //printHistory();
    }

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
            captureState("load file");
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
        captureState("load file");
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

        final var node = graph.addNode(x, y);
        captureState("add node");
        Logger.info("Added Node (" + node + ")");
    }

    @Override
    public void addPointToEdge(Edge edge, Point2D point2D) {
        edge.points.add(point2D);
        captureState("add point to line");
    }

    @Override
    public void updatePointOnEdge(Edge edge, int i, Point2D point2D) {
        edge.points.set(i, point2D);
        captureState("modified point of line");
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
            captureState("add edge");
            Logger.info("Added Edge (" + edge + ")");
        }
    }

    @Override
    public void removeNode(Node node){

        graph.removeNode(node);
        captureState("remove node");
        Logger.info("Removed Node (" + node + ")");
    }

    @Override
    public void setNodeTranslate(Node node, double x, double y) {

        node.setXY(x, y);
        captureState("moved node");
    }

    @Override
    public void removeEdge(Edge edge) {

        graph.removeEdge(edge);
        captureState("remove edge");
        Logger.info("Removed Edge (" + edge + ")");
    }

    @Override
    public void setNodeText(Node node, String text) {

        node.text = text;
        captureState("change node text");
    }

    @Override
    public void setEdgeText(Edge edge, String text) {

        edge.text = text;
        captureState("change edge text");
    }

    @Override
    public void setNodeShape(Node node, eNodeShape shape) {

        node.shape = shape;
        captureState("change node shape");
    }

    @Override
    public void reset() {

        graph = new Graph();
        graphPath = null;
        captureState("reset graph");
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
