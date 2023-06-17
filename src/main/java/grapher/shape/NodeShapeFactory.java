package grapher.shape;

import javafx.scene.shape.Shape;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Factory for creating {@link Shape} from {@link ENodeShape}.
 */
public class NodeShapeFactory {
    static private final Map<ENodeShape, INodeShapeFactory> factoryDictionary = new HashMap<>();

    static {
        factoryDictionary.put(ENodeShape.RECTANGLE, new RectangleFactory());
        factoryDictionary.put(ENodeShape.CIRCLE, new CircleFactory());
        factoryDictionary.put(ENodeShape.RIGHT_TRI, new RightTriFactory());
        factoryDictionary.put(ENodeShape.DIAMOND, new DiamondFactory());
    }

    public static Shape build(@Nullable ENodeShape shapeEnum) {
        return Optional.ofNullable(factoryDictionary.get(shapeEnum))
                .map(INodeShapeFactory::make)
                .orElseGet(() -> {
                    Logger.warn("NodeShapeBuilder: eNodeShape {} has no matching case", shapeEnum);
                    return factoryDictionary.get(ENodeShape.RECTANGLE).make();
                });
    }
}
