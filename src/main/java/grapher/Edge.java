package grapher;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Constructor.
     * @param id
     * @param from
     * @param to
     */
    public Edge(int id, Node from, Node to) {
        this(id, from, to, null);
    }

    /**
     * Constructor.
     * @param id
     * @param from
     * @param to
     * @param text
     */
    public Edge(int id, Node from, Node to, String text) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.text = text;
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
