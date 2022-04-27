package grapher.shape;// CHECKSTYLE:OFF

import javafx.scene.shape.Shape;

/**
 * Common interface for Shape Factory classes.
 */
public interface INodeShapeFactory {
    /**
     * Construct a shape.
     *
     * @return The shape constructed.
     */
    Shape make();
}
