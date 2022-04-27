package grapher.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import grapher.serialization.EdgeSerializer;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a connection between two {@link Node}s.
 */
@JsonSerialize(using = EdgeSerializer.class)
public class Edge {
    /**
     * The unique identifier of the connection.
     */
    public int id;
    /**
     * The source Node of the connection.
     */
    public Node from;
    /**
     * The target Node of the connection.
     */
    public Node to;
    /**
     * The text associated with the connection (description, condition, etc.).
     */
    public String text;

    public List<Point2D> points = new ArrayList<>();

    {
        //points.add(new Point2D(0, 0));
    }

    /**
     * Constructor.
     *
     * @param id   {@link Edge#id}
     * @param from {@link Edge#from}
     * @param to   {@link Edge#to}
     */
    public Edge(int id, Node from, Node to) {
        this(id, from, to, null, new ArrayList<>());
    }

    /**
     * Constructor.
     *
     * @param id   {@link Edge#id}
     * @param from {@link Edge#from}
     * @param to   {@link Edge#to}
     * @param text {@link Edge#text}
     */
    public Edge(int id, Node from, Node to, String text, List<Point2D> points) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.text = text;
        this.points = points;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(from, edge.from) && Objects.equals(to, edge.to) && Objects.equals(text, edge.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public @NotNull String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", text='" + text + '\'' +
                '}';
    }
}
