package grapher;// CHECKSTYLE:OFF

import javafx.beans.binding.DoubleBinding;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.NotNull;

public class EdgeWidget extends Group {
    final @NotNull Edge edge;
    final @NotNull IGraph controller;
    final @NotNull callback updateCallback;
    final double strokeWidthDefault = 2;
    final double strokeWidthWide = 6;

    final Line line = new Line();
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

    public EdgeWidget(@NotNull Edge edge, @NotNull IGraph IGraph, @NotNull callback updateCallback, @NotNull Controller controller) {
        this.edge = edge;
        this.controller = IGraph;
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

        line.setStrokeWidth(strokeWidthDefault);
        line.startXProperty().bind(fromWidget.getLayoutCenterXBinding());
        line.startYProperty().bind(fromWidget.getLayoutCenterYBinding());
        line.endXProperty().bind(toWidget.getLayoutCenterXBinding());
        line.endYProperty().bind(toWidget.getLayoutCenterYBinding());

        label.setText(edge.text);
        label.layoutXProperty().bind(getLayoutCenterXBinding());
        label.layoutYProperty().bind(getLayoutCenterYBinding());

        textArea.setVisible(false);
        textArea.layoutXProperty().bind(getLayoutCenterXBinding());
        textArea.layoutYProperty().bind(getLayoutCenterYBinding());
        textArea.setPrefSize(150,0);


        setOnMouseClicked(e -> {
            if (controller.actionMode == eActionMode.REMOVE) {
                IGraph.removeEdge(edge);
                updateCallback.apply();
            } else {
                textArea.setVisible(true);
                textArea.setText(label.getText());
                textArea.setPromptText("Edge Label");
                textArea.requestFocus();
                textArea.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
                    if(!newFocus){
                        textArea.setVisible(false);
                        IGraph.setEdgeText(edge, textArea.getText());
                        updateCallback.apply();
                    }
                });
            }
            e.consume();
        });
        setOnMouseEntered(e -> line.setStrokeWidth(strokeWidthWide));
        setOnMouseExited(e -> line.setStrokeWidth(strokeWidthDefault));



        this.getChildren().add(line);
        this.getChildren().add(label);
        this.getChildren().add(textArea);
    }
}
