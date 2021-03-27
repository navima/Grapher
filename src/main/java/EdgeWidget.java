import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class EdgeWidget extends Group {
    final int id;
    final Edge edge;
    final UserController controller;
    final callback updateCallback;
    double strokeWidthDefault = 2;
    double strokeWidthWide = 6;

    final Line line = new Line();
    final Label label = new Label();
    final TextArea textArea = new TextArea();

    @FunctionalInterface
    public interface callback { void apply(); }

    public EdgeWidget(int id, Edge edge, UserController controller, callback updateCallback, GUI parentGUI) {
        this.id = id;
        this.edge = edge;
        this.controller = controller;
        this.updateCallback = updateCallback;

        this.getChildren().add(line);
        this.getChildren().add(label);
        this.getChildren().add(textArea);

        textArea.setVisible(false);

        line.setStrokeWidth(strokeWidthDefault);

        var sourceNodeWidget = parentGUI.nodeWidgetMap.get(edge.from);
        var targetNodeWidget = parentGUI.nodeWidgetMap.get(edge.to);
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
            if (parentGUI.actionMode == eActionMode.REMOVE) {
                controller.removeEdge(id);
                updateCallback.apply();
            } else {
                textArea.setVisible(true);
                textArea.setText(label.getText());
                textArea.setPromptText("Edge Label");
                textArea.requestFocus();
                textArea.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
                    if(!newFocus){
                        textArea.setVisible(false);
                        controller.setEdgeText(id, textArea.getText());
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
