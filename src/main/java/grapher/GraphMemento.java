package grapher;

public class GraphMemento implements IMemento<Graph> {
    public GraphMemento(Graph inGraph){
        value = new Graph(inGraph);
    }
    private final Graph value;

    public final Graph getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GraphMemento{" +
                "value=" + value +
                '}';
    }
}
