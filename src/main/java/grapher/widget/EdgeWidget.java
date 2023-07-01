package grapher.widget;// CHECKSTYLE:OFF

import grapher.Controller;
import grapher.EActionMode;
import grapher.IGraph;
import grapher.model.Edge;
import grapher.util.Callback;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.shape.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EdgeWidget extends Parent {
    /**
     * List of points, in order from 'from' to 'to'.
     */
    @Getter
    final private List<EdgePointWidget> pathPoints = new ArrayList<>();
    /**
     * List of paths between points, in order from 'from' to 'to'.
     */
    final private List<Path> paths = new ArrayList<>();
    final private Label label = new Label();
    final private TextArea textArea = new TextArea();
    final private NodeWidget fromWidget;
    final private NodeWidget toWidget;
    @Getter
    private final DoubleBinding layoutCenterXBinding;
    @Getter
    private final DoubleBinding layoutCenterYBinding;
    private final Polyline arrowWidget;

    public EdgeWidget(@NotNull Edge edge, @NotNull IGraph graph, @NotNull Callback updateCallback, @NotNull Controller controller) {

        fromWidget = controller.nodeWidgetMap.get(edge.from);
        toWidget = controller.nodeWidgetMap.get(edge.to);
        arrowWidget = new Polyline(-5, -10, 0, 0, 5, -10);
        arrowWidget.getStyleClass().add("graph-edge-arrow");

        layoutCenterXBinding = new DoubleBinding() {
            {
                bind(fromWidget.getLayoutCenterXBinding(),
                        toWidget.getLayoutCenterXBinding());
            }

            @Override
            protected double computeValue() {
                return pathPoints.stream().mapToDouble(Node::getLayoutX).average().getAsDouble();
            }
        };
        layoutCenterYBinding = new DoubleBinding() {
            {
                bind(fromWidget.getLayoutCenterYBinding(),
                        toWidget.getLayoutCenterYBinding());
            }

            @Override
            protected double computeValue() {
                return pathPoints.stream().mapToDouble(Node::getLayoutY).average().getAsDouble();
            }
        };

        ChangeListener<Object> arrowPositionListener = (observable, oldValue, newValue) -> refreshArrowPosition();


        // Manipulating points
        EdgePointWidget c0 = new EdgePointWidget(edge, -1);
        c0.setMouseTransparent(true);
        c0.layoutXProperty().bind(fromWidget.getLayoutCenterXBinding());
        c0.layoutYProperty().bind(fromWidget.getLayoutCenterYBinding());
        pathPoints.add(c0);
        for (int i = 0; i < edge.points.size(); i++) {
            EdgePointWidget c = new EdgePointWidget(edge, i);
            pathPoints.add(c);
            c.setLayoutX(edge.points.get(i).getX());
            c.setLayoutY(edge.points.get(i).getY());
        }
        EdgePointWidget cN = new EdgePointWidget(edge, -1);
        cN.setMouseTransparent(true);
        cN.layoutXProperty().bind(toWidget.getLayoutCenterXBinding());
        cN.layoutYProperty().bind(toWidget.getLayoutCenterYBinding());
        pathPoints.add(cN);

        for (var point : pathPoints) {
            point.layoutXProperty().addListener(arrowPositionListener);
            point.layoutYProperty().addListener(arrowPositionListener);
        }


        // Edges
        // TODO change this so that position is stored in layout instead of in the "moveTo"
        for (int i = 0; i < pathPoints.size() - 1; i++) {
            final var path = new Path();
            final var currPoint = pathPoints.get(i);
            final var nextPoint = pathPoints.get(i + 1);
            paths.add(path);

            MoveTo moveToStart = new MoveTo();
            moveToStart.xProperty().bind(currPoint.layoutXProperty());
            moveToStart.yProperty().bind(currPoint.layoutYProperty());
            LineTo lineToNext = new LineTo();
            lineToNext.xProperty().bind(nextPoint.layoutXProperty());
            lineToNext.yProperty().bind(nextPoint.layoutYProperty());
            path.getElements().addAll(moveToStart, lineToNext);

            path.getStyleClass().add("graph-edge");

            path.setOnMousePressed(event -> {
                var lineToMouse = new LineTo();
                lineToMouse.setX(event.getX());
                lineToMouse.setY(event.getY());
                path.getElements().add(1, lineToMouse);
                var moveToMouse = new MoveTo();
                moveToMouse.setX(event.getX());
                moveToMouse.setY(event.getY());
                path.getElements().add(2, moveToMouse);
                event.consume();
            });
            path.setOnMouseDragged(event -> {
                var lineToMouse = (LineTo) path.getElements().get(1);
                lineToMouse.setX(event.getX());
                lineToMouse.setY(event.getY());
                var moveToMouse = (MoveTo) path.getElements().get(2);
                moveToMouse.setX(event.getX());
                moveToMouse.setY(event.getY());
                event.consume();
            });
            final int finalI = i;
            path.setOnMouseReleased(mouseEvent -> {
                if (!mouseEvent.isStillSincePress()) {
                    graph.addPointToEdge(edge, finalI, new Point2D(mouseEvent.getX(), mouseEvent.getY()));
                    mouseEvent.consume();
                    updateCallback.call();
                }
            });
        }

        var lastPath = paths.get(paths.size() - 1);
        var moveTo = (MoveTo) lastPath.getElements().get(0);
        var lineTo = (LineTo) lastPath.getElements().get(1);
        arrowWidget.setTranslateX(0);
        arrowWidget.setTranslateY(5);
        arrowWidget.layoutXProperty().bind(moveTo.xProperty().add(lineTo.xProperty()).divide(2));
        arrowWidget.layoutYProperty().bind(moveTo.yProperty().add(lineTo.yProperty()).divide(2));
        updateArrowRotate(moveTo, lineTo);
        var listener = (InvalidationListener) (observable) -> updateArrowRotate(moveTo, lineTo);
        arrowWidget.layoutXProperty().addListener(listener);


        getChildren().addAll(paths);
        getChildren().addAll(pathPoints);


        label.setText(edge.text);
        label.layoutXProperty().bind(getLayoutCenterXBinding());
        label.layoutYProperty().bind(getLayoutCenterYBinding());
        label.getStyleClass().add("graph-edge-label");

        textArea.setVisible(false);
        textArea.layoutXProperty().bind(getLayoutCenterXBinding());
        textArea.layoutYProperty().bind(getLayoutCenterYBinding());
        textArea.setPrefSize(150, 0);
        textArea.getStyleClass().add("graph-edge-textarea");


        setOnMouseClicked(e -> {
            if (controller.actionMode == EActionMode.REMOVE) {
                graph.removeEdge(edge);
                updateCallback.call();
            } else {
                if (e.isStillSincePress()) {
                    textArea.setVisible(true);
                    textArea.setText(label.getText());
                    textArea.setPromptText("Edge Label");
                    textArea.requestFocus();
                    textArea.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
                        if (!newFocus) {
                            textArea.setVisible(false);
                            graph.setEdgeText(edge, textArea.getText());
                            updateCallback.call();
                        }
                    });
                }
            }
            e.consume();
        });

        this.getChildren().add(label);
        this.getChildren().add(textArea);
        this.getChildren().add(arrowWidget);
    }

    private void updateArrowRotate(MoveTo moveTo, LineTo lineTo) {
        var x = moveTo.getX();
        var y = moveTo.getY();
        var xv = lineTo.getX();
        var yv = lineTo.getY();
        var dx = xv - x;
        var dy = yv - y;
        arrowWidget.setRotate(Math.toDegrees(Math.atan2(dy, dx)) - 90);
    }

    private void refreshArrowPosition() {
        // TODO implement function that finds edge of 'to' node.
        /*
        for (int i = paths.size() - 1; i == 0; i--) {
            if (toWidget.getBoundsInParent().intersects(localToParent(paths.get(i).getBoundsInParent()))) {
                label.setText("WÚ");
                Shape intersect = Shape.intersect(paths.get(i), toWidget.getLabel().getShape());
                Logger.info(intersect);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    arrowWidget.setLayoutX(intersect.getLayoutX());
                    arrowWidget.setLayoutY(intersect.getLayoutY());
                    break;
                }
            } else {
                label.setText("");
            }
        }
        */
    }
}
