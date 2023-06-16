package grapher.shape;// CHECKSTYLE:OFF

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * {@link ENodeShape#RECTANGLE} Factory class.
 */
public class RectangleFactory implements INodeShapeFactory {
    @Override
    public Shape make() {
        return new Rectangle(0, 0, 50, 50);
    }
}
