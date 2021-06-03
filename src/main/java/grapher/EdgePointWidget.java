package grapher;

import javafx.scene.shape.Circle;

public class EdgePointWidget extends Circle {
    public EdgePointWidget(Edge parentEdge, int i) {
        this.parentEdge = parentEdge;
        this.i = i;
    }
    public Edge parentEdge;
    public int i;
}
