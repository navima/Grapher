package grapher.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import grapher.serialization.EdgeSerializer;
import javafx.geometry.Point2D;
import lombok.*;

import java.util.List;

/**
 * Represents a connection between two {@link Node}s.
 */
@JsonSerialize(using = EdgeSerializer.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Edge {
    @EqualsAndHashCode.Include
    @NonNull
    public Integer id;
    @NonNull
    public Node from;
    @NonNull
    public Node to;
    public String text;
    public List<Point2D> points;

    public String toString() {
        return "Edge(id=" + this.id + ", from=" + this.from.id + ", to=" + this.to.id + ", text=" + this.text + ", points=" + this.points + ")";
    }
}
