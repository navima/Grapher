public class Node {
    public double x;
    public double y;
    public String text;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Node(){}
    public void setXY(double x, double y) { this.x = x; this.y = y;}

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", text='" + (text!=null ? text.substring(0, 15)+"..." : null) + '\'' +
                '}';
    }
}
