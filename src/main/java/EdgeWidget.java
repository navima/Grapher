import javafx.scene.shape.Line;

public class EdgeWidget extends Line {
    final int id;
    final Edge edge;
    final UserController controller;
    final callback updateCallback;
    double strokeWidthDefault = 2;
    double strokeWidthWide = 6;

    @FunctionalInterface
    public interface callback { void apply(); }

    public EdgeWidget(int id, Edge edge, UserController controller, callback updateCallback, GUI parentGUI) {
        this.id = id;
        this.edge = edge;
        this.controller = controller;
        this.updateCallback = updateCallback;

        setStrokeWidth(strokeWidthDefault);

        setStartX(controller.graph.getNode(edge.from).x);
        setStartY(controller.graph.getNode(edge.from).y);
        setEndX(controller.graph.getNode(edge.to).x);
        setEndY(controller.graph.getNode(edge.to).y);

        setOnMouseClicked(e -> {
            if (parentGUI.actionMode == eActionMode.REMOVE) {
                controller.removeEdge(id);
                updateCallback.apply();
            }
        });
        setOnMouseEntered(e -> setStrokeWidth(strokeWidthWide));
        setOnMouseExited(e -> setStrokeWidth(strokeWidthDefault));
    }
}
