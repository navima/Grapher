package grapher.shape;// CHECKSTYLE:OFF

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * {@link ENodeShape#RIGHT_TRI} Factory class.
 */
public class RightTriFactory implements INodeShapeFactory {
    @Override
    public Shape make() {
        var pRtri = new Polygon();
        pRtri.getPoints().addAll(0., 0., 20., 10., 0., 20.);
        return pRtri;
    }
}
