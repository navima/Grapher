package grapher;// CHECKSTYLE:OFF

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

        this.getChildren().add(line);
        this.getChildren().add(label);
        this.getChildren().add(textArea);

        textArea.setVisible(false);

        line.setStrokeWidth(strokeWidthDefault);

        var sourceNodeWidget = controller.nodeWidgetMap.get(edge.from);
        var targetNodeWidget = controller.nodeWidgetMap.get(edge.to);
        line.setStartX(sourceNodeWidget.getLayoutX()+sourceNodeWidget.button.getWidth()/2);
        line.setStartY(sourceNodeWidget.getLayoutY()+sourceNodeWidget.button.getHeight()/2);
        line.setEndX(targetNodeWidget.getLayoutX()+targetNodeWidget.button.getWidth()/2);
        line.setEndY(targetNodeWidget.getLayoutY()+targetNodeWidget.button.getHeight()/2);

        label.setText(edge.text);
        label.setLayoutX((line.getStartX()+line.getEndX())/2);
        label.setLayoutY((line.getStartY()+line.getEndY())/2);

        textArea.setLayoutX((line.getStartX()+line.getEndX())/2);
        textArea.setLayoutY((line.getStartY()+line.getEndY())/2);
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
    }
}
