package grapher.shape;// CHECKSTYLE:OFF

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * {@link ENodeShape#DIAMOND} Factory class.
 */
public class DiamondFactory implements INodeShapeFactory {
    @Override
    public Shape make() {
        var pDia = new Polygon();
        pDia.getPoints().addAll(0., 10., 10., 20., 20., 10., 10., 0.);
        return pDia;
    }
}
