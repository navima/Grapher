import java.util.Objects;

public class Edge {
    public int from;
    public int to;
    public String text;

    public Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }
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
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", text='" + text + '\'' +
                '}';
    }
}
