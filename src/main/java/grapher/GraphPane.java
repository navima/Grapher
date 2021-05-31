package grapher;// CHECKSTYLE:OFF

import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

public class GraphPane extends Pane {
    public boolean isPanning = false;
    private double dragStartMouseX = 0.0;
    private double dragStartMouseY = 0.0;
    private double dragStartTranslateX = 0.0;
    private double dragStartTranslateY = 0.0;

    public double childTranslateX = 0.0;
    public double childTranslateY = 0.0;

    public GraphPane() {
        setMouseTransparent(false);
        setOnMouseDragged(e -> {
            isPanning = true;
            setChildTranslate(e.getX()-dragStartMouseX+dragStartTranslateX, e.getY()-dragStartMouseY+dragStartTranslateY);
            //System.out.println(e.getX()+" - " + dragStartMouseX+" + "+dragStartTranslateX);
            e.consume();
        });
        setOnMousePressed( e -> {
            dragStartTranslateX = childTranslateX;
            dragStartTranslateY = childTranslateY;
            dragStartMouseX = e.getX();
            dragStartMouseY = e.getY();
        });
        //setOnMouseReleased(e -> isPanning = false);
    }


    public void clear() {getChildren().clear();}
    public void addChild(javafx.scene.@NotNull Node n) {getChildren().add(n); n.setTranslateX(childTranslateX); n.setTranslateY(childTranslateY);}

    public final double getChildTranslateX(){return childTranslateX;}
    public final double getChildTranslateY(){return childTranslateY;}
    public final void setChildTranslateX(double x){for (final var elem : getChildren()) elem.setTranslateX(x); childTranslateX = x;}
    public final void setChildTranslateY(double y){for (final var elem : getChildren()) elem.setTranslateY(y); childTranslateY = y;}
    public final void setChildTranslate(double x,double y){for (final var elem : getChildren()) {elem.setTranslateX(x);elem.setTranslateY(y);} childTranslateX = x; childTranslateY = y;}
}
