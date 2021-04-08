package grapher;

import java.util.Objects;

/**
 * Class representing a node.
 */
public class Node {
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
    public eNodeShape shape;

    /**
     * Default constructor.
     * @param x {@link Node#x}
     * @param y {@link Node#y}
     */
    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * DO NOT USE, ONLY NEEDED FOR DESERIALIZATION.
     */
    public Node(){}

    /**
     * Sets the X and Y coordinates.
     * @param x New X
     * @param y New Y
     */
    public void setXY(double x, double y) { this.x = x; this.y = y;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Double.compare(node.x, x) == 0 && Double.compare(node.y, y) == 0 && Objects.equals(text, node.text) && shape == node.shape;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, text, shape);
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", text='" + (text!=null ? text.substring(0, 15)+"..." : null) + '\'' +
                '}';
    }
}
