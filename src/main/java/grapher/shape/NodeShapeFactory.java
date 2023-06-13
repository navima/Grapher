package grapher.shape;

import javafx.scene.shape.Shape;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        return Optional.ofNullable(factoryDictionary.get(shapeEnum))
                .map(INodeShapeFactory::make)
                .orElseGet(() -> {
                    Logger.warn("NodeShapeBuilder: eNodeShape {} has no matching case", shapeEnum);
                    return factoryDictionary.get(eNodeShape.RECTANGLE).make();
                });
    }
}
