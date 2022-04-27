package grapher.shape;

import javafx.scene.shape.Shape;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating {@link Shape} from {@link eNodeShape}.
 */
public class NodeShapeFactory {
    static private final Map<eNodeShape, INodeShapeFactory> factoryDictionary = new HashMap<>();

    static {
        factoryDictionary.put(eNodeShape.RECTANGLE, new RectangleFactory());
        factoryDictionary.put(eNodeShape.CIRCLE, new CircleFactory());
        factoryDictionary.put(eNodeShape.RIGHT_TRI, new RightTriFactory());
        factoryDictionary.put(eNodeShape.DIAMOND, new DiamondFactory());
    }

    public static @Nullable Shape build(@Nullable eNodeShape shapeEnum) {
        var factory = factoryDictionary.get(shapeEnum);
        if (factory == null) {
            if (shapeEnum == null)
                Logger.error("NodeShapeBuilder: eNodeShape " + "null" + " has no matching case.");
            else
                Logger.error("NodeShapeBuilder: eNodeShape " + shapeEnum.name() + " has no matching case.");
            return factoryDictionary.get(eNodeShape.RECTANGLE).make();
        } else
            return factory.make();
    }
}
