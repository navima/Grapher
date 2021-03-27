import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class NodeShapeBuilder {
    static Shape build(eNodeShape shapeEnum){
        if (shapeEnum == eNodeShape.CIRCLE) {
            return new Circle(0, 0, 75);
        } else if (shapeEnum == eNodeShape.RECTANGLE) {
            return new Rectangle(0, 0, 50, 50);
        } else if (shapeEnum == eNodeShape.RIGHT_TRI) {
            var temp = new Polygon();
            temp.getPoints().addAll(0.0, 0.0, 20.0, 10.0, 0.0, 20.0);
            return temp;
        }
        return null;
    }
}
