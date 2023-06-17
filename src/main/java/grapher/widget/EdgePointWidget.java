package grapher.widget;

import grapher.model.Edge;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import lombok.Setter;

import java.util.Objects;

public class EdgePointWidget extends Parent {

    public Edge parentEdge;
    public int i;
    public Circle circle = new Circle();
    @Setter
    private EventHandler<ActionEvent> onAction;

    public EdgePointWidget(Edge parentEdge, int i) {
        this.parentEdge = parentEdge;
        this.i = i;
        var lab = new Label("" + i + " : " + parentEdge.text);
        this.getChildren().addAll(circle);//, lab);
        circle.setRadius(1);
        circle.getStyleClass().add("graph-edge-point");
        this.getStyleClass().addListener((ListChangeListener<? super String>) change -> {
            change.next();
            if (change.wasRemoved())
                circle.getStyleClass().removeAll(change.getRemoved());
            else if (change.wasAdded())
                circle.getStyleClass().addAll(change.getAddedSubList());
        });

        setOnMouseClicked(e -> {
            if (e.isStillSincePress()) {
                var ae = new ActionEvent();
                onAction.handle(ae);
                if (ae.isConsumed())
                    e.consume();
            }
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
