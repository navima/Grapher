package grapher;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectangleFactory implements INodeShapeFactory {
    @Override
    public Shape make() {
        return new Rectangle(0, 0, 50, 50);
    }
}
