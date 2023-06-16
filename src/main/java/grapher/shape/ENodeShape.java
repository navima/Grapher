package grapher.shape;

import grapher.model.Node;
import lombok.Getter;

/**
 * Enum describing the shape of a {@link Node}.
 * For UI purposes, use with {@link NodeShapeFactory}
 */
public enum ENodeShape {
    RECTANGLE("Rectangle"),
    CIRCLE("Circle"),
    RIGHT_TRI("Right pointing triangle"),
    DIAMOND("Diamond");

    @Getter
    private final String friendlyName;

    @Override
    public String toString() {
        return friendlyName;
    }

    ENodeShape(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
