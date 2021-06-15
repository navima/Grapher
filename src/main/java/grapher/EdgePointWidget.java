package grapher;

import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

import java.util.Objects;

public class EdgePointWidget extends Parent {

    public Edge parentEdge;
    public int i;
    public Circle circle = new Circle();

    public EdgePointWidget(Edge parentEdge, int i) {
        this.parentEdge = parentEdge;
        this.i = i;
        var lab = new Label("" + i + " : "+parentEdge.text);
        this.getChildren().addAll(circle);//, lab);
        circle.setRadius(1);
        circle.getStyleClass().add("graph-edge-point");
        this.getStyleClass().addListener((ListChangeListener<? super String>) change -> {
            change.next();
            if (change.wasRemoved())
                circle.getStyleClass().removeAll(change.getRemoved());
            else if(change.wasAdded())
                circle.getStyleClass().addAll(change.getAddedSubList());
        });

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgePointWidget that = (EdgePointWidget) o;
        return i != -1 && i == that.i && Objects.equals(parentEdge, that.parentEdge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentEdge, i);
    }
}
