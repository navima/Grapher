package grapher;// CHECKSTYLE:OFF

import javafx.beans.binding.DoubleBinding;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.shape.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EdgeWidget extends Parent {
    final @NotNull Edge edge;
    final @NotNull IGraph graph;
    final @NotNull callback updateCallback;
    final List<Path> paths = new ArrayList<>();
    final List<EdgePointWidget> pathPoints = new ArrayList<>();
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

    public List<EdgePointWidget> getPathPoints() {
        return pathPoints;
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


        // Manipulating points
        EdgePointWidget c0 = new EdgePointWidget(edge, -1);
        c0.setMouseTransparent(true);
        c0.layoutXProperty().bind(fromWidget.getLayoutCenterXBinding());
        c0.layoutYProperty().bind(fromWidget.getLayoutCenterYBinding());
        pathPoints.add(c0);
        for (int i = 0; i < edge.points.size(); i++){
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
        for (var elem : pathPoints){
            //style
            elem.setRadius(1);
            elem.getStyleClass().add("graph-edge-point");
        }


        // Edges
        for(int i = 0; i < pathPoints.size() - 1; i++){
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
                var lineToMouse = (LineTo)path.getElements().get(1);
                lineToMouse.setX(event.getX());
                lineToMouse.setY(event.getY());
                var moveToMouse = (MoveTo)path.getElements().get(2);
                moveToMouse.setX(event.getX());
                moveToMouse.setY(event.getY());
                event.consume();
            });
            final int finalI = i;
            path.setOnMouseReleased(mouseEvent -> {
                if(!mouseEvent.isStillSincePress()){
                    graph.addPointToEdge(edge, finalI, new Point2D(mouseEvent.getX(), mouseEvent.getY()));
                    mouseEvent.consume();
                    updateCallback.apply();
                }
            });
        }

        getChildren().addAll(paths);
        getChildren().addAll(pathPoints);


        label.setText(edge.text);
        label.layoutXProperty().bind(getLayoutCenterXBinding());
        label.layoutYProperty().bind(getLayoutCenterYBinding());

        textArea.setVisible(false);
        textArea.layoutXProperty().bind(getLayoutCenterXBinding());
        textArea.layoutYProperty().bind(getLayoutCenterYBinding());
        textArea.setPrefSize(150,0);



        setOnMouseClicked(e -> {
            if (controller.actionMode == eActionMode.REMOVE) {
                graph.removeEdge(edge);
                updateCallback.apply();
            } else {
                if(e.isStillSincePress()){
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
            }
            e.consume();
        });

        this.getChildren().add(label);
        this.getChildren().add(textArea);
    }
}
