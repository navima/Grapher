package grapher;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class DiamondFactory implements INodeShapeFactory{
    @Override
    public Shape make() {
        var pDia = new Polygon();
        pDia.getPoints().addAll(0., 10., 10., 20., 20., 10., 10., 0.);
        return pDia;
    }
}
