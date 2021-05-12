package grapher;// CHECKSTYLE:OFF

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NodeWidget extends Group {
    final int id;
    final @NotNull Node value;

    final Button button = new Button();
    final TextArea textArea = new TextArea();

    private EventHandler<ActionEvent> onActionHandler;
    public void setOnAction(EventHandler<ActionEvent> onAction) {onActionHandler = onAction;}
    private BiConsumer<Double, Double> onDragEndedHandler;
    public void setOnDragEnded(BiConsumer<Double, Double> onDragEnded) {onDragEndedHandler = onDragEnded;}
    private Consumer<String> onTextChangedHandler;
    public void setOnTextChanged(Consumer<String> onTextChanged) {onTextChangedHandler = onTextChanged;}
    private Consumer<eNodeShape> onNodeShapeChangedHandler;
    public void setOnNodeShapeChanged(Consumer<eNodeShape> onNodeShapeChanged) {onNodeShapeChangedHandler = onNodeShapeChanged;}


    @FunctionalInterface
    public interface callback { void apply(); }

    private double dragStartMouseX = 0.0;
    private double dragStartMouseY = 0.0;
    private double dragStartTranslateX = 0.0;
    private double dragStartTranslateY = 0.0;
    private boolean wasDragged = false;
    public NodeWidget(@NotNull Node n, int id, final @NotNull callback updateCallback) {
        super();
        this.getChildren().add(button);
        this.getChildren().add(textArea);

        textArea.setVisible(false);

        button.setShape(NodeShapeFactory.build(n.shape));

        button.setText(n.text);
        this.id = id;
        value = n;
        setLayoutX(n.x);
        setLayoutY(n.y);

        var contextMenu = new ContextMenu();
        for (final var elem : eNodeShape.values()) {
            var contextMenuItem = new MenuItem(elem.toString());
            contextMenuItem.setOnAction(e -> {
                onNodeShapeChangedHandler.accept(elem);
                //graphWrapper.setNodeShape(id, elem);
                updateCallback.apply();
            });
            contextMenu.getItems().add(contextMenuItem);
        }
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
                onDragEndedHandler.accept(getLayoutX(), getLayoutY());
                //graphWrapper.setNodeTranslate(id, getLayoutX(), getLayoutY());
                updateCallback.apply();
                wasDragged = false;
            }
        });
        button.setOnAction(e -> {
            onActionHandler.handle(e);
            if (!e.isConsumed()) {
                textArea.setMaxSize(button.getWidth(),button.getHeight());
                textArea.setVisible(true);
                textArea.setText(button.getText());
                textArea.setPromptText("Node Label");
                textArea.requestFocus();
                textArea.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
                    if(!newFocus){
                        textArea.setVisible(false);
                        onTextChangedHandler.accept(textArea.getText());
                        //graphWrapper.setNodeText(id, textArea.getText());
                        updateCallback.apply();
                        //button.setText(textArea.getText());
                    }
                });
            }
        });
    }
}
