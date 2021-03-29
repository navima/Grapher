import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

public class NodeWidget extends Group {
    final int id;
    final Node value;
    final UserController controller;

    final Button button = new Button();
    final TextArea textArea = new TextArea();


    @FunctionalInterface
    public interface callback { void apply(); }

    private double dragStartMouseX = 0.0;
    private double dragStartMouseY = 0.0;
    private double dragStartTranslateX = 0.0;
    private double dragStartTranslateY = 0.0;
    private boolean wasDragged = false;
    public NodeWidget(Node n, int id, UserController controller, final callback updateCallback, final GUI parentGUI) {
        super();
        this.getChildren().add(button);
        this.getChildren().add(textArea);

        textArea.setVisible(false);

        button.setShape(NodeShapeBuilder.build(n.shape));

        button.setText(n.text);
        this.controller = controller;
        this.id = id;
        value = n;
        setLayoutX(n.x);
        setLayoutY(n.y);

        var contextMenu = new ContextMenu();
        var contextMenuItem1 = new MenuItem("Rectangle");
        contextMenuItem1.setOnAction(e -> {
            controller.setNodeShape(id, eNodeShape.RECTANGLE);
            updateCallback.apply();
        });
        var contextMenuItem2 = new MenuItem("Circle");
        contextMenuItem2.setOnAction(e -> {
            controller.setNodeShape(id, eNodeShape.CIRCLE);
            updateCallback.apply();
        });
        var contextMenuItem3 = new MenuItem("Right Triangle");
        contextMenuItem3.setOnAction(e -> {
            controller.setNodeShape(id, eNodeShape.RIGHT_TRI);
            updateCallback.apply();
        });
        contextMenu.getItems().addAll(contextMenuItem1,contextMenuItem2,contextMenuItem3);
        button.setContextMenu(contextMenu);
        button.setOnMousePressed(e -> {
            dragStartMouseX = e.getSceneX();
            dragStartMouseY = e.getSceneY();
            dragStartTranslateX = getLayoutX();
            dragStartTranslateY = getLayoutY();
            e.consume();
        });
        button.setOnMouseDragged(e -> {
            setLayoutX(dragStartTranslateX+e.getSceneX()-dragStartMouseX);
            setLayoutY(dragStartTranslateY+e.getSceneY()-dragStartMouseY);
            wasDragged = true;
            e.consume();
            //System.out.println("HELP! IM BEING DRAGGED!!");
        });
        button.setOnMouseReleased(e -> {
            if (wasDragged) {
                controller.setNodeTranslate(id, getLayoutX(), getLayoutY());
                updateCallback.apply();
                wasDragged = false;
            }
        });
        button.setOnAction(e -> {
            if (parentGUI.actionMode == eActionMode.REMOVE) {
                controller.removeNode(id);
                updateCallback.apply();
            } else if (parentGUI.actionMode == eActionMode.EDGE_ADD) {
                if (parentGUI.edgeBeingAdded) {
                    try {
                        controller.addEdge(parentGUI.edgeStartNode.id, id);
                    } catch (Exception ignore) { }
                    parentGUI.edgeStartNode = null;
                    parentGUI.edgeBeingAdded = false;
                    updateCallback.apply();
                } else {
                    parentGUI.edgeBeingAdded = true;
                    parentGUI.edgeStartNode = this;
                }
            } else {
                textArea.setMaxSize(button.getWidth(),button.getHeight());
                textArea.setVisible(true);
                textArea.setText(button.getText());
                textArea.setPromptText("Node Label");
                textArea.requestFocus();
                textArea.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
                    if(!newFocus){
                        textArea.setVisible(false);
                        controller.setNodeText(id, textArea.getText());
                        updateCallback.apply();
                        //button.setText(textArea.getText());
                    }
                });
            }
        });
    }
}
