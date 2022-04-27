package grapher.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import grapher.serialization.NodeSerializer;
import grapher.shape.eNodeShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class representing a node.
 */
@JsonSerialize(using = NodeSerializer.class)
public class Node {
    /**
     * Unique identifier of node.
     */
    public int id;
    /**
     * X position of node.
     */
    public double x;
    /**
     * Y position of node.
     */
    public double y;
    /**
     * Text (label) associated with node.
     */
    public String text;
    /**
     * Shape of node.
     */
    public eNodeShape shape = eNodeShape.RECTANGLE;
    /**
     * Edges associated with.
     */
    public HashSet<Edge> edges = new HashSet<>();

    /**
     * Default constructor.
     *
     * @param x  {@link Node#x}
     * @param y  {@link Node#y}
     * @param id {@link Node#id}
     */
    public Node(double x, double y, int id) {
        this(x, y, id, eNodeShape.RECTANGLE, null);
    }

    /**
     * Constructor.
     *
     * @param x     {@link Node#x}
     * @param y     {@link Node#y}
     * @param id    {@link Node#id}
     * @param shape {@link Node#shape}
     * @param text  {@link Node#text}
     */
    public Node(double x, double y, int id, eNodeShape shape, String text) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.shape = shape;
        this.text = text;
    }

    /**
     * DO NOT USE, ONLY NEEDED FOR DESERIALIZATION.
     */
    public Node() {
    }

    public static Node copyWithoutEdges(Node other) {
        return new Node(other.x, other.y, other.id, other.shape, other.text);
    }

    /**
     * Sets the X and Y coordinates.
     *
     * @param x New X
     * @param y New Y
     */
    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Double.compare(node.x, x) == 0 && Double.compare(node.y, y) == 0 && Objects.equals(text, node.text) && shape == node.shape;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public @NotNull String toString() {
        return "{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", text=\"" + (text != null ? text.replaceAll("\n", " ") : "") + "\"" +
                ", edges=" + edges.stream().map(edge -> edge.id).collect(Collectors.toList()) +
                '}';
    }
}
