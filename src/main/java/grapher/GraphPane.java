package grapher;// CHECKSTYLE:OFF

import com.sun.javafx.collections.TrackableObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GraphPane extends Pane {
    private double dragStartMouseX = 0.0;
    private double dragStartMouseY = 0.0;
    private double dragStartTranslateX = 0.0;
    private double dragStartTranslateY = 0.0;

    private double childTranslateX = 0.0;
    private double childTranslateY = 0.0;

    private double marqueeStartX;
    private double marqueeStartY;

    public final Group g = new Group();
    public final Rectangle marquee = new Rectangle();
    public final List<Node> selectionList = new ArrayList<>();
    public final ObservableList<Node> selection = new ModifiableObservableListBase<Node>() {
        @Override
        public Node get(int i) {
            return selectionList.get(i);
        }

        @Override
        public int size() {
            return selectionList.size();
        }

        @Override
        protected void doAdd(int i, Node node) {
            selectionList.add(i, node);
            node.getStyleClass().add("selected");
        }

        @Override
        protected Node doSet(int i, Node node) {
            Node n = selectionList.get(i);
            n.getStyleClass().remove("selected");
            selectionList.set(i, node);
            node.getStyleClass().add("selected");
            return node;
        }

        @Override
        protected Node doRemove(int i) {
            Node n = selectionList.get(i);
            selectionList.remove(i);
            n.getStyleClass().remove("selected");
            return n;
        }
    };

    public GraphPane() {
        g.getStyleClass().add("menubar");
        getChildren().add(g);

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
                for(var child : g.getChildren()){
                    if(marquee.getBoundsInLocal().intersects(child.getBoundsInParent()))
                        selection.add(child);
                }
                e.consume();
            }
            else if(e.isSecondaryButtonDown()){
                setChildTranslate(e.getX()-dragStartMouseX+dragStartTranslateX, e.getY()-dragStartMouseY+dragStartTranslateY);
                e.consume();
            }
        });
        setOnMouseReleased(e -> {
            if(e.isStillSincePress() && isFocused()){
                getChildren().remove(marquee);
                selection.clear();
            }
            System.out.println("selected: ");
            selection.forEach(System.out::println);
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
                dragStartMouseX = e.getX();
                dragStartMouseY = e.getY();
                e.consume();
            }
        });
        //setOnMouseReleased(e -> isPanning = false);
        setOnScroll(scrollEvent -> {
            g.setScaleX(scrollEvent.getDeltaY()/scrollEvent.getMultiplierY()*0.1+g.getScaleX());
            g.setScaleY(scrollEvent.getDeltaY()/scrollEvent.getMultiplierY()*0.1+g.getScaleY());
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
    public final void setChildTranslateX(double x){for (final var elem : g.getChildren()) elem.setTranslateX(x); childTranslateX = x;}
    public final void setChildTranslateY(double y){for (final var elem : g.getChildren()) elem.setTranslateY(y); childTranslateY = y;}
    public final void setChildTranslate(double x,double y){for (final var elem : g.getChildren()) {elem.setTranslateX(x);elem.setTranslateY(y);} childTranslateX = x; childTranslateY = y;}

    public static class GraphPaneSlot extends Parent {
        public final GraphPane parent;
        private double dragStartMouseX = 0.0;
        private double dragStartMouseY = 0.0;
        private double dragStartTranslateX = 0.0;
        private double dragStartTranslateY = 0.0;
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
                dragStartMouseX = e.getX();
                dragStartMouseY = e.getY();
                dragStartTranslateX = value.getLayoutX();
                dragStartTranslateY = value.getLayoutY();
                System.out.println("pressed");
                e.consume();
            });
            setOnMouseDragged(e -> {
                value.setLayoutX(dragStartTranslateX+e.getX()-dragStartMouseX);
                value.setLayoutY(dragStartTranslateY+e.getY()-dragStartMouseY);
                System.out.println("dragged");
                e.consume();
                //System.out.println("HELP! IM BEING DRAGGED!!");
            });
            setOnMouseReleased(e -> {
                if (!e.isStillSincePress()){
                    if(!(getOnMoved() == null))
                        getOnMoved().handle(new ActionEvent());
                    System.out.println("released");
                    e.consume();
                }
            });

        }
        private final Node value;
        private EventHandler<ActionEvent> onMoved;
        private EventHandler<ActionEvent> onModified;

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
    }
}

