package grapher.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import grapher.serialization.EdgeSerializer;
import javafx.geometry.Point2D;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a connection between two {@link Node}s.
 */
@JsonSerialize(using = EdgeSerializer.class)
@ToString
public class Edge {
    public int id;
    public Node from;
    public Node to;
    public String text;

    public List<Point2D> points;

    public Edge(int id, Node from, Node to) {
        this(id, from, to, null, new ArrayList<>());
    }

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
}
