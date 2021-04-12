package grapher;// CHECKSTYLE:OFF

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.tinylog.Logger;

public class NodeShapeBuilder {
    static Shape build(eNodeShape shapeEnum){
        if(shapeEnum == null)
            shapeEnum = eNodeShape.RECTANGLE;
        switch (shapeEnum) {
            case CIRCLE:
                return new Circle(0, 0, 75);
            case RECTANGLE:
                return new Rectangle(0, 0, 50, 50);
            case RIGHT_TRI:
                var pRtri = new Polygon();
                pRtri.getPoints().addAll(0., 0., 20., 10., 0., 20.);
                return pRtri;
            case DIAMOND:
                var pDia = new Polygon();
                pDia.getPoints().addAll(0., 10., 10., 20., 20., 10., 10., 0.);
                return pDia;
            default:
                Logger.error("NodeShapeBuilder: eNodeShape "+shapeEnum.name()+" has no matching case.");
        }
        return null;
    }
}
