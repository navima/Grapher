package grapher.memento;

import grapher.model.Graph;
import lombok.Getter;
import lombok.ToString;

@ToString
public class GraphMemento implements IMemento<Graph> {
    @Getter
    private final Graph value;

    public GraphMemento(Graph inGraph) {
        value = new Graph(inGraph);
    }
}
