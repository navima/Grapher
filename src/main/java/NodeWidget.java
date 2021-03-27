import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class NodeWidget extends Button {
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
        super(n.text);
        this.controller = controller;
        this.id = id;
        value = n;
        setLayoutX(n.x);
        setLayoutY(n.y);


        setOnMousePressed(e -> {
            dragStartMouseX = e.getSceneX();
            dragStartMouseY = e.getSceneY();
            dragStartTranslateX = getLayoutX();
            dragStartTranslateY = getLayoutY();
            e.consume();
        });
        setOnMouseDragged(e -> {
            setLayoutX(dragStartTranslateX+e.getSceneX()-dragStartMouseX);
            setLayoutY(dragStartTranslateY+e.getSceneY()-dragStartMouseY);
            wasDragged = true;
            e.consume();
            //System.out.println("HELP! IM BEING DRAGGED!!");
        });
        setOnMouseReleased(e -> {
            if (wasDragged) {
                controller.setNodeTranslate(id, getLayoutX(), getLayoutY());
                updateCallback.apply();
                wasDragged = false;
            }
        });
        setOnAction(e -> {
            if (parentGUI.actionMode == eActionMode.REMOVE) {
                controller.removeNode(id);
                updateCallback.apply();
            } else if (parentGUI.actionMode == eActionMode.EDGE_ADD) {
                if (parentGUI.edgeBeingAdded) {
                    controller.addEdge(parentGUI.edgeStartNode.id, id);
                    parentGUI.edgeStartNode = null;
                    parentGUI.edgeBeingAdded = false;
                    updateCallback.apply();
                } else {
                    parentGUI.edgeBeingAdded = true;
                    parentGUI.edgeStartNode = this;
                }
            } else {
                final var textbox = new TextArea(value.text);
                textbox.setMinSize(100, 50);
                textbox.setMaxSize(100, 50);
                System.out.println(textbox.getLayoutX());
                getChildren().add(textbox);
            }
        });
    }
}
