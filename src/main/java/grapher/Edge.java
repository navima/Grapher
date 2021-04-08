package grapher;

import java.util.Objects;

/**
 * Represents a connection between two {@link Node}s.
 */
public class Edge {
    /**
     * The source grapher.Node of the connection.
     */
    public int from;
    /**
     * The target grapher.Node of the connection.
     */
    public int to;
    /**
     * The text associated with the connection (description, condition, etc.).
     */
    public String text;

    /**
     * This constructor constructs a construct representing a meta-construct.
     * @param from {@link Edge#from}
     * @param to {@link Edge#to}
     */
    public Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }

    /**
     * DO NOT USE, ONLY NEEDED FOR DESERIALIZATION.
     */
    public Edge(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from == edge.from && to == edge.to && Objects.equals(text, edge.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, text);
    }

    @Override
    public String toString() {
        return "grapher.Edge{" +
                "from=" + from +
                ", to=" + to +
                ", text='" + text + '\'' +
                '}';
    }
}
