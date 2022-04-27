package grapher.shape;

import grapher.model.Node;

/**
 * Enum describing the shape of a {@link Node}.
 * For UI purposes, use with {@link NodeShapeFactory}
 */
public enum eNodeShape {
    /**
     * Rectangle.
     */
    RECTANGLE("Rectangle"),
    /**
     * Circle.
     */
    CIRCLE("Circle"),
    /**
     * Right-pointing triangle.
     */
    RIGHT_TRI("Right pointing triangle"),
    /**
     * Diamond.
     */
    DIAMOND("Diamond");

    private final String friendlyName;

    @Override
    public String toString() {
        return friendlyName;
    }

    eNodeShape(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
