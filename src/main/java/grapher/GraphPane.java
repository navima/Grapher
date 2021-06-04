package grapher;// CHECKSTYLE:OFF

import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GraphPane extends Region {
    private double dragStartMouseX = 0.0;
    private double dragStartMouseY = 0.0;
    private double dragStartTranslateX = 0.0;
    private double dragStartTranslateY = 0.0;

    private double childTranslateX = 0.0;
    private double childTranslateY = 0.0;

    private double marqueeStartX;
    private double marqueeStartY;
    
    private double zoomMultiplier = 0.1;

    public final Pane g = new Pane();
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
        g.getStyleClass().add("graph-pane-inner");
        getChildren().add(g);
        g.setPrefSize(150, 150);

        marquee.getStyleClass().add("marquee");

        setMouseTransparent(false);
        setOnMouseDragged(e -> {
            if(e.isPrimaryButtonDown()){
                var xmin = Math.min(e.getX(), marqueeStartX);
                var ymin = Math.min(e.getY(), marqueeStartY);
                var xmax = Math.max(e.getX(), marqueeStartX);
                var ymax = Math.max(e.getY(), marqueeStartY);
                marquee.setX(xmin);
                marquee.setY(ymin);
                marquee.setWidth(xmax-xmin);
                marquee.setHeight(ymax-ymin);
                selection.clear();
                var gTrans = g.getLocalToParentTransform();
                for (var child : g.getChildren()){
                    if (marquee.getBoundsInLocal().intersects(gTrans.transform(child.getBoundsInParent())))
                        selection.add((GraphPaneSlot) child);
                }
                e.consume();
            }
            else if(e.isSecondaryButtonDown()){
                setChildTranslate(e.getSceneX()-dragStartMouseX+dragStartTranslateX, e.getSceneY()-dragStartMouseY+dragStartTranslateY);
                e.consume();
            }
        });
        setOnMouseReleased(e -> {
            if(e.isStillSincePress() && isFocused()){
                getChildren().remove(marquee);
                selection.clear();
            }
            getChildren().remove(marquee);
            requestFocus();
            e.consume();
        });
        setOnMousePressed( e -> {
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
            }
            else if (e.isSecondaryButtonDown()){
                dragStartTranslateX = childTranslateX;
                dragStartTranslateY = childTranslateY;
                dragStartMouseX = e.getSceneX();
                dragStartMouseY = e.getSceneY();
                e.consume();
            }
        });
        setOnScroll(scrollEvent -> {
            var ctx = getChildTranslateX();
            var cty = getChildTranslateY();
            var glx = g.getLayoutX();
            var gly = g.getLayoutY();
            var gsx = g.getScaleX();
            var gsy = g.getScaleY();
            var mx = scrollEvent.getX();
            var my = scrollEvent.getY();
            setChildTranslate(
                    ctx+(glx-mx)/gsx,
                    cty+(gly-my)/gsy);
            g.setLayoutX(scrollEvent.getX());
            g.setLayoutY(scrollEvent.getY());
            g.setScaleX(scrollEvent.getDeltaY()/scrollEvent.getMultiplierY()*getZoomMultiplier()+g.getScaleX());
            g.setScaleY(scrollEvent.getDeltaY()/scrollEvent.getMultiplierY()*getZoomMultiplier()+g.getScaleY());
        });
    }

    public void clear() {g.getChildren().clear(); selection.clear();}
    public GraphPaneSlot addChild(javafx.scene.@NotNull Node n) {
        GraphPaneSlot graphPaneSlot = new GraphPaneSlot(this, n);
        g.getChildren().add(graphPaneSlot);
        n.setTranslateX(childTranslateX);
        n.setTranslateY(childTranslateY);
        return graphPaneSlot;
    }

    public final double getChildTranslateX(){return childTranslateX;}
    public final double getChildTranslateY(){return childTranslateY;}
    public final void setChildTranslate(double x,double y) {
        for (final var elem : g.getChildren()) {
            ((GraphPaneSlot)elem).value.setTranslateX(x);
            ((GraphPaneSlot)elem).value.setTranslateY(y);
        }
        childTranslateX = x;
        childTranslateY = y;
    }

    public double getZoomMultiplier() {
        return zoomMultiplier;
    }
    public void setZoomMultiplier(double zoomMultiplier) {
        this.zoomMultiplier = zoomMultiplier;
    }


    public static class GraphPaneSlot extends Parent {
        private boolean draggable = true;
        public final GraphPane parent;
        private final Node value;
        private EventHandler<ActionEvent> onMoved;
        private EventHandler<ActionEvent> onModified;
        private double dragStartMouseX = 0.0;
        private double dragStartMouseY = 0.0;
        private double dragStartLayoutX = 0.0;
        private double dragStartLayoutY = 0.0;
        public GraphPaneSlot(GraphPane parent, Node node) {
            this.parent = parent;
            value = node;
            getChildren().add(node);
            this.getStyleClass().addListener((ListChangeListener<? super String>) change -> {
                change.next();
                if (change.wasRemoved())
                    value.getStyleClass().removeAll(change.getRemoved());
                else if(change.wasAdded())
                    value.getStyleClass().addAll(change.getAddedSubList());
            });
            setOnMousePressed(e -> {
                if(draggable){
                    if(!parent.selection.contains(this))
                        parent.selection.add(this);
                    parent.selection.forEach(elem -> elem.recordMousePressed(e));
                    e.consume();
                }
            });
            setOnMouseDragged(e -> {
                if(draggable) {
                    parent.selection.stream().filter(GraphPaneSlot::isDraggable).forEach(elem -> {
                        elem.value.setLayoutX(elem.dragStartLayoutX +e.getX()-dragStartMouseX);
                        elem.value.setLayoutY(elem.dragStartLayoutY +e.getY()-dragStartMouseY);
                    });
                    e.consume();
                    //System.out.println("HELP! IM BEING DRAGGED!!");
                }
            });
            setOnMouseReleased(e -> {
                if (draggable) {
                    if (!e.isStillSincePress()){
                        var temp = new ArrayList<>(parent.selection);
                        temp.forEach(elem -> {
                            if(!(elem.getOnMoved() == null))
                                elem.getOnMoved().handle(new ActionEvent());
                        });
                        e.consume();
                    }
                }
            });
        }

        private void recordMousePressed(javafx.scene.input.MouseEvent e) {
            dragStartMouseX = e.getX();
            dragStartMouseY = e.getY();
            dragStartLayoutX = value.getLayoutX();
            dragStartLayoutY = value.getLayoutY();
        }


        public Node getValue() {
            return value;
        }
        public EventHandler<ActionEvent> getOnMoved() {
            return onMoved;
        }
        public void setOnMoved(EventHandler<ActionEvent> onMoved) {
            this.onMoved = onMoved;
        }
        public EventHandler<ActionEvent> getOnModified() {
            return onModified;
        }
        public void setOnModified(EventHandler<ActionEvent> onModified) {
            this.onModified = onModified;
        }

        public boolean isDraggable() {
            return draggable;
        }

        public void setDraggable(boolean draggable) {
            this.draggable = draggable;
        }
    }
}

