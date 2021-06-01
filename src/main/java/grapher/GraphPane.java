package grapher;// CHECKSTYLE:OFF

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

public class GraphPane extends Pane {
    private double dragStartMouseX = 0.0;
    private double dragStartMouseY = 0.0;
    private double dragStartTranslateX = 0.0;
    private double dragStartTranslateY = 0.0;

    private double childTranslateX = 0.0;
    private double childTranslateY = 0.0;

    public final Group g = new Group();

    public GraphPane() {
        g.getStyleClass().add("menubar");
        getChildren().add(g);

        setMouseTransparent(false);
        setOnMouseDragged(e -> {
            setChildTranslate(e.getX()-dragStartMouseX+dragStartTranslateX, e.getY()-dragStartMouseY+dragStartTranslateY);
            e.consume();
        });
        setOnMousePressed( e -> {
            dragStartTranslateX = childTranslateX;
            dragStartTranslateY = childTranslateY;
            dragStartMouseX = e.getX();
            dragStartMouseY = e.getY();
        });
        //setOnMouseReleased(e -> isPanning = false);
        setOnScroll(scrollEvent -> {
            g.setScaleX(scrollEvent.getDeltaY()/scrollEvent.getMultiplierY()*0.1+g.getScaleX());
            g.setScaleY(scrollEvent.getDeltaY()/scrollEvent.getMultiplierY()*0.1+g.getScaleY());
        });
    }

    public void clear() {g.getChildren().clear();}
    public void addChild(javafx.scene.@NotNull Node n) {g.getChildren().add(n); n.setTranslateX(childTranslateX); n.setTranslateY(childTranslateY);}

    public final double getChildTranslateX(){return childTranslateX;}
    public final double getChildTranslateY(){return childTranslateY;}
    public final void setChildTranslateX(double x){for (final var elem : g.getChildren()) elem.setTranslateX(x); childTranslateX = x;}
    public final void setChildTranslateY(double y){for (final var elem : g.getChildren()) elem.setTranslateY(y); childTranslateY = y;}
    public final void setChildTranslate(double x,double y){for (final var elem : g.getChildren()) {elem.setTranslateX(x);elem.setTranslateY(y);} childTranslateX = x; childTranslateY = y;}
}
