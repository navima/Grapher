import java.util.Objects;

public class Node {
    public double x;
    public double y;
    public String text;
    public eNodeShape shape;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Node(){}
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
