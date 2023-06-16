package grapher.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import grapher.serialization.NodeSerializer;
import grapher.shape.ENodeShape;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;

/**
 * Class representing a node.
 */
@JsonSerialize(using = NodeSerializer.class)
@NoArgsConstructor
@ToString
public class Node {
    public int id;
    public double x;
    public double y;
    public String text;
    public ENodeShape shape = ENodeShape.RECTANGLE;
    public HashSet<Edge> edges = new HashSet<>();

    public Node(double x, double y, int id) {
        this(x, y, id, ENodeShape.RECTANGLE, null);
    }

    public Node(double x, double y, int id, ENodeShape shape, String text) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.shape = shape;
        this.text = text;
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
}
