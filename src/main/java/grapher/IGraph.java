package grapher;

import grapher.memento.ICaretaker;
import grapher.model.Edge;
import grapher.model.Graph;
import grapher.model.Node;
import grapher.shape.ENodeShape;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

/**
 * The interface for a {@link Graph} object.
 */
public interface IGraph extends ICaretaker {

    /**
     * Saves graph if path is set.
     *
     * @return Whether path is set
     * @throws IOException On IOException
     */
    boolean save() throws IOException;

    /**
     * Saves graph to JSON.
     *
     * @param file The source File
     * @return Always True
     * @throws IOException On IOException
     */
    boolean save(File file) throws IOException;

    /**
     * Loads graph JSON.
     *
     * @param file The source File
     * @return Whether File was null.
     * @throws IOException On IOException
     */
    boolean load(@Nullable File file) throws IOException;

    /**
     * Loads graph JSON.
     *
     * @param src The source URL
     * @throws IOException On IOException
     */
    void load(@NotNull URL src) throws IOException;

    /**
     * Load the default startup graph.
     */
    void loadDefault();

    /**
     * Get a readonly view of the nodes.
     *
     * @return readonly view of the nodes
     */
    Set<Node> getNodes();

    /**
     * Get a readonly view of the edges.
     *
     * @return readonly view of the edges.
     */
    Set<Edge> getEdges();

    /**
     * Adds a Node to graph.
     *
     * @param x {@link Node#x}
     * @param y {@link Node#y}
     */
    void addNode(double x, double y);

    void addPointToEdge(Edge edge, int index, Point2D point2D);

    void updatePointOnEdge(Edge edge, int i, Point2D point2D);

    void removePointFromEdge(Edge edge, int i);

    void setGraphName(String text);

    /**
     * Thrown when an invalid operation has been requested. (eg. adding Edge to nonexistent Node).
     */
    class InvalidOperationException extends Exception {
    }

    /**
     * Adds grapher.model.Edge to graph.
     *
     * @param from grapher.model.Edge source
     * @param to   grapher.model.Edge destination
     * @throws InvalidOperationException On invalid Node IDs
     */
    void addEdge(Node from, Node to) throws InvalidOperationException;

    /**
     * Removes Node from graph.
     *
     * @param node Node to remove
     */
    void removeNode(Node node);

    /**
     * Sets the location of a Node.
     *
     * @param node Node to act on
     * @param x    New X
     * @param y    New Y
     */
    void setNodeTranslate(Node node, double x, double y);

    /**
     * Removes edge from graph.
     *
     * @param edge Edge to remove
     */
    void removeEdge(Edge edge);

    /**
     * Sets the text of a Node.
     *
     * @param node Node to act on
     * @param text New text
     */
    void setNodeText(Node node, String text);

    /**
     * Sets the text of an Edge.
     *
     * @param edge Edge to act on
     * @param text New text
     */
    void setEdgeText(Edge edge, String text);

    /**
     * Sets the shape of a Node.
     *
     * @param node  Node to act on
     * @param shape New shape
     */
    void setNodeShape(Node node, ENodeShape shape);

    /**
     * Resets controller to default state. (blank graph, no path).
     */
    void reset();
}
