package grapher;

import com.fasterxml.jackson.databind.ObjectMapper;
import grapher.memento.GraphMemento;
import grapher.memento.HistoryElement;
import grapher.model.Edge;
import grapher.model.Graph;
import grapher.model.Node;
import grapher.shape.ENodeShape;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wraps graph object and provides convenience methods and history management.
 */
public class GraphManipulator implements IGraph {
    private final TaggedLogger logger;
    public final int id;
    public Graph graph = new Graph();
    @Nullable
    public File graphPath = null;
    public List<HistoryElement<GraphMemento>> history = new ArrayList<>();
    public int historyPosition = 0;

    public GraphManipulator(int id) {
        this.id = id;
        logger = Logger.tag("GraphManipulator" + id);
    }

    @Override
    public List<GraphMemento> getHistory() {
        return history.stream().map(HistoryElement::value).toList();
    }

    @Override
    public void restoreState(GraphMemento memento) {
        graph = new Graph(memento.getValue());
    }

    public void printHistory() {
        var sb = new StringBuilder();
        sb.append("The current history:\n");
        for (int i = history.size() - 1; i >= 0; i--) {
            if (i == historyPosition)
                sb.append("X " + history.get(i).label() + " : " + history.get(i).value() + "\n");
            else
                sb.append("- " + history.get(i).label() + " : " + history.get(i).value() + "\n");
        }
        System.out.println(sb);
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
    public void redo() {
        if (history.isEmpty() || (historyPosition + 1) > (history.size() - 1))
            return;
        var nextState = history.get(++historyPosition);
        restoreState(nextState.value());
        //printHistory();
    }

    @Override
    public void captureState() {
        captureState(null);
    }

    public void captureState(String label) {
        if (history.size() - 1 > historyPosition) {
            logger.info("History is stale. Taking first {} elements (from {})", historyPosition + 1, history.size());
            history = history.stream().limit(historyPosition + 1).collect(Collectors.toList());
        }
        history.add(new HistoryElement<>(label, graph.getState()));
        historyPosition = history.size() - 1;
        //printHistory();
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
        logger.info("Added Node ({})", node);
    }

    @Override
    public void addPointToEdge(Edge edge, int index, Point2D point2D) {
        edge.points.add(index, point2D);
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
            logger.warn("Tried to connect Node with itself");
            return;
        }
        if (from == null || to == null) {
            logger.warn("Tried to connect from or to null Node");
            throw new InvalidOperationException();
        } else {
            var edge = graph.addEdge(from, to);
            captureState("add edge");
            logger.info("Added Edge ({})", edge);
        }
    }

    @Override
    public void removeNode(Node node) {

        graph.removeNode(node);
        captureState("remove node");
        logger.info("Removed Node ({})", node);
    }

    @Override
    public void removePointFromEdge(Edge edge, int n) {
        graph.removeEdgeNode(edge, n);
        captureState("remove edge point");
        logger.info("Removed {}-th Point from Edge ({})", n, edge);
    }

    @Override
    public void setGraphName(String text) {
        graph.name = text;
        captureState("change name");
        logger.info("Changed graph name to {}", text);
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
        logger.info("Removed Edge ({})", edge);
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
    public void setNodeShape(Node node, ENodeShape shape) {

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
        GraphManipulator that = (GraphManipulator) o;
        return Objects.equals(graph, that.graph) && Objects.equals(graphPath, that.graphPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph, graphPath);
    }
}
