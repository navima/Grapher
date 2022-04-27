package grapher.widget;// CHECKSTYLE:OFF

import grapher.model.Node;
import grapher.shape.NodeShapeFactory;
import grapher.shape.eNodeShape;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class NodeWidget extends Parent {
    private static final String DEFAULT_STYLE_CLASS = "graph-node";
    public final @NotNull Node value;

    final Label label = new Label();
    final TextArea textArea = new TextArea();

    private EventHandler<ActionEvent> onActionHandler;

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        onActionHandler = onAction;
    }

    private Consumer<String> onTextChangedHandler;

    public void setOnTextChanged(Consumer<String> onTextChanged) {
        onTextChangedHandler = onTextChanged;
    }

    private Consumer<eNodeShape> onNodeShapeChangedHandler;

    public void setOnNodeShapeChanged(Consumer<eNodeShape> onNodeShapeChanged) {
        onNodeShapeChangedHandler = onNodeShapeChanged;
    }

    private final DoubleBinding layoutCenterX = new DoubleBinding() {
        {
            super.bind(layoutXProperty());
        }

        @Override
        protected double computeValue() {
            return getLayoutX() + label.getBoundsInLocal().getWidth() / 2;
        }
    };

    public DoubleBinding getLayoutCenterXBinding() {
        return layoutCenterX;
    }

    private final DoubleBinding layoutCenterY = new DoubleBinding() {
        {
            super.bind(layoutYProperty());
        }

        @Override
        protected double computeValue() {
            return getLayoutY() + label.getBoundsInLocal().getHeight() / 2;
        }
    };

    public DoubleBinding getLayoutCenterYBinding() {
        return layoutCenterY;
    }

    @Override
    public javafx.scene.Node getStyleableNode() {
        return label;
    }


    /**
     * Invalidate callback.
     */
    @FunctionalInterface
    public interface callback {
        /**
         * Invalidate method.
         */
        void apply();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeWidget other) {
            return value.equals(other.value);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return value.text;
    }

    public NodeWidget(@NotNull Node n, final @NotNull callback updateCallback) {
        super();
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        this.getStyleClass().addListener((ListChangeListener<? super String>) change -> {
            change.next();
            if (change.wasRemoved())
                label.getStyleClass().removeAll(change.getRemoved());
            else if (change.wasAdded())
                label.getStyleClass().addAll(change.getAddedSubList());
        });

        //this.getChildren().add(button);
        this.getChildren().add(label);
        this.getChildren().add(textArea);

        textArea.setVisible(false);
        textArea.setPromptText("Node Label");
        textArea.prefWidthProperty().bind(label.widthProperty());
        textArea.prefHeightProperty().bind(label.heightProperty());
        textArea.setText(n.text);

        label.textProperty().bind(textArea.textProperty());


        label.setShape(NodeShapeFactory.build(n.shape));
        label.getStyleClass().add("button");
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
        label.setContextMenu(contextMenu);
        label.setOnMouseClicked(e -> {
            var ae = new ActionEvent();
            onActionHandler.handle(ae);
            if (!ae.isConsumed()) {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    e.consume();
                    textArea.setVisible(true);
                    textArea.requestFocus();
                    textArea.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
                        if (!newFocus) {
                            textArea.setVisible(false);
                            onTextChangedHandler.accept(textArea.getText());
                            updateCallback.apply();
                        }
                    });
                }
            }
        });
    }
}
