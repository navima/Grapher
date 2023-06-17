package grapher.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import grapher.serialization.NodeSerializer;
import grapher.shape.ENodeShape;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node {
    @EqualsAndHashCode.Include
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
}
