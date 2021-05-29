package grapher;// CHECKSTYLE:OFF

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.shape.*;
import org.jetbrains.annotations.NotNull;

public class EdgeWidget extends Group {
    final @NotNull Edge edge;
    final @NotNull IGraph graph;
    final @NotNull callback updateCallback;
    final double strokeWidthDefault = 2;
    final double strokeWidthWide = 6;

    final Path path = new Path();
    final Label label = new Label();
    final TextArea textArea = new TextArea();
    final NodeWidget fromWidget;
    final NodeWidget toWidget;


    private final DoubleBinding layoutCenterX;

    public DoubleBinding getLayoutCenterXBinding() {
        return layoutCenterX;
    }
    private final DoubleBinding layoutCenterY;

    public DoubleBinding getLayoutCenterYBinding() {
        return layoutCenterY;
    }

    /**
     * Invalidate callback.
     */
    @FunctionalInterface
    public interface callback {
        /**
         * Invalidate method.
         */
        void apply(); }

    boolean wasPathDragged = false;
    public EdgeWidget(@NotNull Edge edge, @NotNull IGraph graph, @NotNull callback updateCallback, @NotNull Controller controller) {
        this.edge = edge;
        this.graph = graph;
        this.updateCallback = updateCallback;

        fromWidget = controller.nodeWidgetMap.get(edge.from);
        toWidget = controller.nodeWidgetMap.get(edge.to);

        layoutCenterX = new DoubleBinding() {
            {
                bind(
                        fromWidget.getLayoutCenterXBinding(),
                        toWidget.getLayoutCenterXBinding());
            }
            @Override
            protected double computeValue() {
                return (fromWidget.getLayoutCenterXBinding().get()+toWidget.getLayoutCenterXBinding().get())/2;
            }
        };
        layoutCenterY = new DoubleBinding() {
            {
                bind(
                        fromWidget.getLayoutCenterYBinding(),
                        toWidget.getLayoutCenterYBinding());
            }
            @Override
            protected double computeValue() {
                return (fromWidget.getLayoutCenterYBinding().get()+toWidget.getLayoutCenterYBinding().get())/2;
            }
        };


        path.setStrokeWidth(strokeWidthDefault);
        var moveToStart = new MoveTo();
        moveToStart.xProperty().bind(fromWidget.getLayoutCenterXBinding());
        moveToStart.yProperty().bind(fromWidget.getLayoutCenterYBinding());
        var lineToEnd = new LineTo();
        lineToEnd.xProperty().bind(toWidget.getLayoutCenterXBinding());
        lineToEnd.yProperty().bind(toWidget.getLayoutCenterYBinding());
        path.getElements().addAll(moveToStart);
        for (var point : edge.points) {
            var lineToNext = new LineTo(point.getX(), point.getY());
            var moveToNext = new MoveTo(point.getX(), point.getY());
            path.getElements().addAll(lineToNext, moveToNext);
        }
        path.getElements().addAll(lineToEnd);
        /*
        line.startXProperty().bind(fromWidget.getLayoutCenterXBinding());
        line.startYProperty().bind(fromWidget.getLayoutCenterYBinding());
        line.endXProperty().bind(toWidget.getLayoutCenterXBinding());
        line.endYProperty().bind(toWidget.getLayoutCenterYBinding());
        */

        label.setText(edge.text);
        label.layoutXProperty().bind(getLayoutCenterXBinding());
        label.layoutYProperty().bind(getLayoutCenterYBinding());

        textArea.setVisible(false);
        textArea.layoutXProperty().bind(getLayoutCenterXBinding());
        textArea.layoutYProperty().bind(getLayoutCenterYBinding());
        textArea.setPrefSize(150,0);

        path.setOnMouseDragged(mouseEvent -> {
            if(edge.points.size()<1) {
                graph.addPointToEdge(edge, new Point2D(mouseEvent.getX(), mouseEvent.getY()));
                mouseEvent.consume();
                updateCallback.apply();
                return;
            }
            ((LineTo)path.getElements().get(1)).setX(mouseEvent.getX());
            ((LineTo)path.getElements().get(1)).setY(mouseEvent.getY());
            ((MoveTo)path.getElements().get(2)).setX(mouseEvent.getX());
            ((MoveTo)path.getElements().get(2)).setY(mouseEvent.getY());
            wasPathDragged = true;
            mouseEvent.consume();
        });
        path.setOnMouseReleased(mouseEvent -> {
            if(wasPathDragged){
                graph.updatePointOnEdge(edge, 0, new Point2D(mouseEvent.getX(), mouseEvent.getY()));
                //graph.addPointToEdge(edge, new Point2D(mouseEvent.getX(), mouseEvent.getY()));
                wasPathDragged = false;
                mouseEvent.consume();
                updateCallback.apply();
            }
        });


        setOnMouseClicked(e -> {
            if (controller.actionMode == eActionMode.REMOVE) {
                graph.removeEdge(edge);
                updateCallback.apply();
            } else {
                textArea.setVisible(true);
                textArea.setText(label.getText());
                textArea.setPromptText("Edge Label");
                textArea.requestFocus();
                textArea.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
                    if(!newFocus){
                        textArea.setVisible(false);
                        graph.setEdgeText(edge, textArea.getText());
                        updateCallback.apply();
                    }
                });
            }
            e.consume();
        });
        setOnMouseEntered(e -> path.setStrokeWidth(strokeWidthWide));
        setOnMouseExited(e -> path.setStrokeWidth(strokeWidthDefault));



        this.getChildren().add(path);
        this.getChildren().add(label);
        this.getChildren().add(textArea);
    }
}
