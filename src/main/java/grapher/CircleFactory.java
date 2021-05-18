package grapher;// CHECKSTYLE:OFF

import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * {@link eNodeShape#CIRCLE} Factory class.
 */
public class CircleFactory implements  INodeShapeFactory{
    @Override
    public Shape make() {
        return new Circle(0, 0, 75);
    }
}
