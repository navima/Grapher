package grapher;// CHECKSTYLE:OFF

import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.AccessibleRole;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NodeWidget extends Parent {
    private static final String DEFAULT_STYLE_CLASS = "graph-node";
    public final @NotNull Node value;

    final Label label = new Label();
    final TextArea textArea = new TextArea();

    private EventHandler<ActionEvent> onActionHandler;
    public void setOnAction(EventHandler<ActionEvent> onAction) {onActionHandler = onAction;}
    private BiConsumer<Double, Double> onDragEndedHandler;
    public void setOnDragEnded(BiConsumer<Double, Double> onDragEnded) {onDragEndedHandler = onDragEnded;}
    private Consumer<String> onTextChangedHandler;
    public void setOnTextChanged(Consumer<String> onTextChanged) {onTextChangedHandler = onTextChanged;}
    private Consumer<eNodeShape> onNodeShapeChangedHandler;
    public void setOnNodeShapeChanged(Consumer<eNodeShape> onNodeShapeChanged) {onNodeShapeChangedHandler = onNodeShapeChanged;}

    private final DoubleBinding layoutCenterX = new DoubleBinding() {
        {
            super.bind(layoutXProperty());
        }
        @Override
        protected double computeValue() {
            return getLayoutX()+label.getBoundsInLocal().getWidth()/2;
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
            return getLayoutY()+label.getBoundsInLocal().getHeight()/2;
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
        void apply(); }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof NodeWidget other) {
            return value.equals(other.value);
        }
        return super.equals(obj);
    }

    public NodeWidget(@NotNull Node n, final @NotNull callback updateCallback) {
        super();
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        this.getStyleClass().addListener((ListChangeListener<? super String>) change -> {
            change.next();
            if (change.wasRemoved())
                label.getStyleClass().removeAll(change.getRemoved());
            else if(change.wasAdded())
                label.getStyleClass().addAll(change.getAddedSubList());
        });

        //this.getChildren().add(button);
        this.getChildren().add(textArea);
        this.getChildren().add(label);

        textArea.setVisible(false);

        label.setShape(NodeShapeFactory.build(n.shape));
        label.getStyleClass().add("button");

        label.setText(n.text);
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
            textArea.setMaxSize(label.getWidth(), label.getHeight());
            textArea.setVisible(true);
            textArea.setText(label.getText());
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
        });
    }
}
