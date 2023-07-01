package grapher.widget;// CHECKSTYLE:OFF

import grapher.util.StyleChangeListenerFactory;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GraphPane is a zoomable, pannable pane that lets its children be moved by the user.
 * Is preserves child Layout, panning is stored in Translate.
 */
public class GraphPane extends Region {
    private double dragStartMouseSceneX = 0.0;
    private double dragStartMouseSceneY = 0.0;
    private double dragStartChildTranslateX = 0.0;
    private double dragStartChildTranslateY = 0.0;

    @Getter
    private double childTranslateX = 0.0;
    @Getter
    private double childTranslateY = 0.0;

    private double marqueeStartX;
    private double marqueeStartY;

    @Getter
    @Setter
    private double zoomMultiplier = 0.1;

    public final Pane innerCanvas = new Pane();
    public final Rectangle marquee = new Rectangle();
    public final List<GraphPaneSlot> selectionList = new ArrayList<>();
    public final ObservableList<GraphPaneSlot> selection = new ModifiableObservableListBase<>() {
        @Override
        public GraphPaneSlot get(int i) {
            return selectionList.get(i);
        }

        @Override
        public int size() {
            return selectionList.size();
        }

        @Override
        protected void doAdd(int i, GraphPaneSlot node) {
            selectionList.add(i, node);
            node.getStyleClass().add("selected");
        }

        @Override
        protected GraphPaneSlot doSet(int i, GraphPaneSlot node) {
            var n = selectionList.get(i);
            n.getStyleClass().remove("selected");
            selectionList.set(i, node);
            node.getStyleClass().add("selected");
            return node;
        }

        @Override
        protected GraphPaneSlot doRemove(int i) {
            var n = selectionList.get(i);
            selectionList.remove(i);
            n.getStyleClass().remove("selected");
            return n;
        }
    };

    public GraphPane() {
        getStyleClass().add("graph-pane-outer");
        innerCanvas.getStyleClass().add("graph-pane-inner");
        getChildren().add(innerCanvas);
        innerCanvas.setPrefSize(1, 1);
        marquee.setMouseTransparent(true);
        marquee.getStyleClass().add("marquee");
        var hintLabel = new Label("'Home' to reset view");
        getChildren().add(hintLabel);

        setMouseTransparent(false);
        setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                var xmin = Math.min(e.getX(), marqueeStartX);
                var ymin = Math.min(e.getY(), marqueeStartY);
                var xmax = Math.max(e.getX(), marqueeStartX);
                var ymax = Math.max(e.getY(), marqueeStartY);
                marquee.setX(xmin);
                marquee.setY(ymin);
                marquee.setWidth(xmax - xmin);
                marquee.setHeight(ymax - ymin);
                selection.clear();
                var gTrans = innerCanvas.getLocalToParentTransform();
                for (var child : innerCanvas.getChildren()) {
                    if (marquee.getBoundsInLocal().intersects(gTrans.transform(child.getBoundsInParent())))
                        selection.add((GraphPaneSlot) child);
                }
                e.consume();
            } else if (e.isSecondaryButtonDown()) {
                Transform paneToCanvasTransform = null;
                try {
                    paneToCanvasTransform = innerCanvas.getLocalToParentTransform().createInverse();
                } catch (NonInvertibleTransformException ex) {
                    throw new RuntimeException(ex);
                }
                var newMouseSceneX = e.getSceneX();
                var newMouseSceneY = e.getSceneY();
                var dragStartMouseCanvasPos = paneToCanvasTransform.transform(dragStartMouseSceneX, dragStartMouseSceneY);
                var newMouseCanvasPos = paneToCanvasTransform.transform(newMouseSceneX, newMouseSceneY);
                var mouseCanvasDelta = newMouseCanvasPos.subtract(dragStartMouseCanvasPos);
                setChildrenTranslate(mouseCanvasDelta.getX() + dragStartChildTranslateX, mouseCanvasDelta.getY() + dragStartChildTranslateY);
                e.consume();
            }
        });
        setOnMouseReleased(e -> {
            if (e.isStillSincePress() && isFocused()) {
                getChildren().remove(marquee);
                selection.clear();
            }
            getChildren().remove(marquee);
            requestFocus();
            e.consume();
        });
        setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown()) {
                getChildren().remove(marquee);
                marquee.setX(e.getX());
                marquee.setY(e.getY());
                marquee.setWidth(0);
                marquee.setHeight(0);
                marqueeStartX = e.getX();
                marqueeStartY = e.getY();
                getChildren().add(marquee);
                e.consume();
            } else if (e.isSecondaryButtonDown()) {
                dragStartChildTranslateX = childTranslateX;
                dragStartChildTranslateY = childTranslateY;
                dragStartMouseSceneX = e.getSceneX();
                dragStartMouseSceneY = e.getSceneY();
                e.consume();
            }
        });
        setOnScroll(scrollEvent -> {
            var ctx = getChildTranslateX();
            var cty = getChildTranslateY();
            var clx = innerCanvas.getLayoutX();
            var cly = innerCanvas.getLayoutY();
            var csx = innerCanvas.getScaleX();
            var csy = innerCanvas.getScaleY();
            var mx = scrollEvent.getX();
            var my = scrollEvent.getY();
            // move inner canvas layout to EXACT mouse location
            innerCanvas.setLayoutX(mx);
            innerCanvas.setLayoutY(my);
            // set childrentranslate so that visually the children stay where they were
            setChildrenTranslate(
                    ctx + (clx - mx) / csx,
                    cty + (cly - my) / csy);
            // update scale of inner canvas
            innerCanvas.setScaleX(Math.max(0.01, Math.signum(scrollEvent.getDeltaY()) * zoomMultiplier + csx));
            innerCanvas.setScaleY(Math.max(0.01, Math.signum(scrollEvent.getDeltaY()) * zoomMultiplier + csy));
        });
        setOnKeyPressed(e -> {
            Logger.info(e);
            if (e.getCode() == KeyCode.HOME) {
                setChildrenTranslate(0, 0);
                innerCanvas.setScaleX(1);
                innerCanvas.setScaleY(1);
                innerCanvas.setLayoutX(0);
                innerCanvas.setLayoutY(0);
                e.consume();
            }
        });
    }

    public void clear() {
        innerCanvas.getChildren().clear();
        selection.clear();
    }

    public GraphPaneSlot addChild(javafx.scene.@NotNull Node n) {
        GraphPaneSlot graphPaneSlot = new GraphPaneSlot(this, n);
        innerCanvas.getChildren().add(graphPaneSlot);
        n.setTranslateX(childTranslateX);
        n.setTranslateY(childTranslateY);
        return graphPaneSlot;
    }

    private void setChildrenTranslate(double x, double y) {
        for (final var elem : innerCanvas.getChildren()) {
            ((GraphPaneSlot) elem).value.setTranslateX(x);
            ((GraphPaneSlot) elem).value.setTranslateY(y);
        }
        childTranslateX = x;
        childTranslateY = y;
    }

    public List<GraphPaneSlot> getSelection() {
        return Collections.unmodifiableList(selectionList);
    }

    public void deselectAll() {
        selection.clear();
    }

    public void select(GraphPaneSlot node) {
        if (!selection.contains(node)) {
            deselectAll();
            selection.add(node);
        }
    }

    public void selectAlso(GraphPaneSlot node) {
        if (!selection.contains(node)) {
            selection.add(node);
        }
    }

    public void deselect(GraphPaneSlot node) {
        selection.remove(node);
    }

    /**
     * Widget that contains a child of the GraphPane, with dragging behaviour.
     */
    public static class GraphPaneSlot extends Parent {
        @Getter
        @Setter
        private boolean draggable = true;
        private final GraphPane parent;
        @Getter
        private final Node value;
        @Getter
        @Setter
        private EventHandler<ActionEvent> onMoved;
        private double dragStartMouseX = 0.0;
        private double dragStartMouseY = 0.0;
        private double dragStartLayoutX = 0.0;
        private double dragStartLayoutY = 0.0;

        public GraphPaneSlot(GraphPane parent, Node node) {
            this.parent = parent;
            value = node;
            getChildren().add(node);
            this.getStyleClass().addListener(StyleChangeListenerFactory.copierListener(value));
            setOnMousePressed(e -> {
                if (draggable) {
                    select();
                    parent.selection.forEach(elem -> elem.recordMouseAndLayoutLocation(e));
                    e.consume();
                }
            });
            setOnMouseDragged(e -> {
                if (draggable) {
                    parent.selection.stream().filter(GraphPaneSlot::isDraggable).forEach(elem -> {
                        elem.value.setLayoutX(elem.dragStartLayoutX + e.getX() - dragStartMouseX);
                        elem.value.setLayoutY(elem.dragStartLayoutY + e.getY() - dragStartMouseY);
                    });
                    e.consume();
                }
            });
            setOnMouseReleased(e -> {
                if (draggable) {
                    if (!e.isStillSincePress()) {
                        var temp = new ArrayList<>(parent.selection);
                        temp.forEach(elem -> {
                            if (elem.getOnMoved() != null)
                                elem.getOnMoved().handle(new ActionEvent());
                        });
                        e.consume();
                    }
                }
            });
        }

        private void recordMouseAndLayoutLocation(javafx.scene.input.MouseEvent e) {
            dragStartMouseX = e.getX();
            dragStartMouseY = e.getY();
            dragStartLayoutX = value.getLayoutX();
            dragStartLayoutY = value.getLayoutY();
        }

        /**
         * Sets this widget as the selected widget on the parent GraphPane.
         */
        public void select() {
            parent.select(this);
        }

        public void deselect() {
            parent.deselect(this);
        }

        public boolean isSelected() {
            return parent.selection.contains(this);
        }
    }
}

