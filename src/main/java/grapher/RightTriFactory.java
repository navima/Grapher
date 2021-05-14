package grapher;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class RightTriFactory implements INodeShapeFactory{
    @Override
    public Shape make() {
        var pRtri = new Polygon();
        pRtri.getPoints().addAll(0., 0., 20., 10., 0., 20.);
        return pRtri;
    }
}