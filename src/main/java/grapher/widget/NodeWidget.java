package grapher.widget;// CHECKSTYLE:OFF

import grapher.model.Node;
import grapher.shape.ENodeShape;
import grapher.shape.NodeShapeFactory;
import grapher.util.Callback;
import grapher.util.StyleChangeListenerFactory;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
public class NodeWidget extends Parent {
    private static final String DEFAULT_STYLE_CLASS = "graph-node";
    @EqualsAndHashCode.Include
    public final @NotNull Node value;
    @Getter
    private final Label label = new Label();
    private final TextArea textArea = new TextArea();
    @Setter
    private EventHandler<ActionEvent> onAction;
    @Setter
    private Consumer<String> onTextChanged;
    @Setter
    private Consumer<ENodeShape> onNodeShapeChanged;
    @Getter
    private final DoubleBinding layoutCenterXBinding = new DoubleBinding() {
        {
            super.bind(layoutXProperty());
        }

        @Override
        protected double computeValue() {
            return getLayoutX() + label.getBoundsInLocal().getWidth() / 2;
        }
    };
    @Getter
    private final DoubleBinding layoutCenterYBinding = new DoubleBinding() {
        {
            super.bind(layoutYProperty());
        }

        @Override
        protected double computeValue() {
            return getLayoutY() + label.getBoundsInLocal().getHeight() / 2;
        }
    };

    @Override
    public javafx.scene.Node getStyleableNode() {
        return label;
    }

    @Override
    public String toString() {
        return value.text;
    }

    public NodeWidget(@NotNull Node n, @NotNull Callback updateCallback) {
        super();
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        this.getStyleClass().addListener(StyleChangeListenerFactory.copierListener(label));

        this.getChildren().add(label);
        this.getChildren().add(textArea);

        textArea.setVisible(false);
        textArea.setPromptText("Node Label");
        // AUTOSIZE WIDTH
        // Set Max and Min Width to PREF_SIZE so that the TextField is always PREF
        textArea.setMinWidth(Region.USE_PREF_SIZE);
        textArea.setMaxWidth(Region.USE_PREF_SIZE);
        textArea.prefRowCountProperty().bind(Bindings.createIntegerBinding(() -> StringUtils.countMatches(textArea.getText(), '\n') + 1, textArea.textProperty()));
        textArea.textProperty().addListener((ov, prevText, currText) -> {
            // Do this in a Platform.runLater because of Textfield has no padding at first time and so on
            Platform.runLater(() -> {
                Text text = new Text(currText);
                text.setFont(textArea.getFont()); // Set the same font, so the size is the same
                double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                        + textArea.getPadding().getLeft() + textArea.getPadding().getRight() // Add the padding of the TextField
                        + 15; // Add some spacing
                textArea.setPrefWidth(width); // Set the width
                textArea.positionCaret(textArea.getCaretPosition()); // If you remove this line, it flashes a little bit
            });
        });
        textArea.setText(n.text);
        textArea.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
            if (!newFocus) {
                textArea.setVisible(false);
                onTextChanged.accept(textArea.getText());
                updateCallback.call();
            }
        });
        textArea.getStyleClass().add("graph-node-textarea");

        label.textProperty().bind(textArea.textProperty());
        label.setShape(NodeShapeFactory.build(n.shape));
        label.getStyleClass().add("graph-node-label");

        value = n;
        setLayoutX(n.x);
        setLayoutY(n.y);

        var contextMenu = new ContextMenu();
        for (final var nodeShape : ENodeShape.values()) {
            var contextMenuItem = new MenuItem(nodeShape.getFriendlyName());
            contextMenuItem.setOnAction(e -> {
                onNodeShapeChanged.accept(nodeShape);
                updateCallback.call();
            });
            contextMenu.getItems().add(contextMenuItem);
        }
        label.setContextMenu(contextMenu);
        label.setOnMouseClicked(e -> {
            var ae = new ActionEvent();
            onAction.handle(ae);
            if (!ae.isConsumed()) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    e.consume();
                    textArea.setVisible(true);
                    textArea.requestFocus();
                }
            }
        });
    }
}
