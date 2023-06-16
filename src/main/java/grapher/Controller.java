package grapher;// CHECKSTYLE:OFF

import grapher.model.Node;
import grapher.widget.EdgeWidget;
import grapher.widget.GraphPane;
import grapher.widget.NodeWidget;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Controller responsible for handling user actions.
 */
public class Controller {
    /**
     * The manipulated graph.
     */
    final IGraph graphWrapper;
    /**
     * The GUI we are handling the events of.
     */
    public GUI gui; //TODO: Loosen the coupling.

    /**
     * Default constructor.
     *
     * @param graphWrapper The graphWrapper we are manipulating.
     */
    public Controller(IGraph graphWrapper) {
        this.graphWrapper = graphWrapper;
    }

    public void graphPaneOnMouseClicked(@NotNull MouseEvent e) {
        if (actionMode == EActionMode.NODE_ADD) {
            if (e.isStillSincePress()) { // don't
                try {
                    var invTrans = gui.graphPane.g.getLocalToParentTransform().createInverse();
                    var res = invTrans.transform(e.getX(), e.getY());
                    graphWrapper.addNode(
                            res.getX() - gui.graphPane.getChildTranslateX(),
                            res.getY() - gui.graphPane.getChildTranslateY());
                    updateGraphPaneContents();

                } catch (NonInvertibleTransformException nonInvertibleTransformException) {
                    Logger.error(nonInvertibleTransformException);
                }
            }
        }
        gui.graphPane.requestFocus();
    }

    public void fileMenuNewFileHandler(ActionEvent actionEvent) {
        graphWrapper.reset();
        updateGraphPaneContents();
    }

    public void fileMenuSaveHandler(ActionEvent actionEvent) {
        try {
            var res = graphWrapper.save();
            if (!res)
                graphWrapper.save(showFilePrompt("Save", EFileActionType.SAVE));
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }

    public void fileMenuSaveAsHandler(ActionEvent actionEvent) {
        try {
            graphWrapper.save(showFilePrompt("Save As", EFileActionType.SAVE));
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }

    public void fileMenuLoadHandler(ActionEvent actionEvent) {
        try {
            var res = graphWrapper.load(showFilePrompt("Load From", EFileActionType.LOAD));
            if (res)
                updateGraphPaneContents();
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }

    private void activateToolbarButton(ToggleButton b, EActionMode actionMode) {
        this.actionMode = actionMode;
        b.setSelected(true);
    }

    public void panHandler(ActionEvent e) {
        activateToolbarButton(gui.bPan, EActionMode.PAN);
    }

    public void addNodeHandler(ActionEvent e) {
        activateToolbarButton(gui.bAddN, EActionMode.NODE_ADD);
    }

    public void addEdgeHandler(ActionEvent e) {
        activateToolbarButton(gui.bAddE, EActionMode.EDGE_ADD);
    }

    public void removeNodeEdgeHandler(ActionEvent e) {
        activateToolbarButton(gui.bRemove, EActionMode.REMOVE);
    }

    public void undoHandler(ActionEvent actionEvent) {
        graphWrapper.undo();
        updateGraphPaneContents();
    }

    public void redoHandler(ActionEvent actionEvent) {
        graphWrapper.redo();
        updateGraphPaneContents();
    }

    /**
     * The type of action performed on a file (save/load).
     */
    public enum EFileActionType {
        SAVE, LOAD
    }

    public @Nullable File showFilePrompt(String title, @NotNull Controller.EFileActionType type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Json Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setTitle(title);
        //noinspection EnhancedSwitchMigration
        switch (type) {
            case LOAD:
                return fileChooser.showOpenDialog(new Stage());
            case SAVE:
                return fileChooser.showSaveDialog(new Stage());
            default:
                throw new IllegalArgumentException();
        }
    }

    @NotNull
    public EActionMode actionMode = EActionMode.PAN;
    @Nullable NodeWidget edgeStartNode = null;
    boolean edgeBeingAdded = false;

    public final HashMap<Node, NodeWidget> nodeWidgetMap = new HashMap<>();

    void updateGraphPaneContents() {
        var oldSelection = gui.graphPane.getSelection().stream().map(GraphPane.GraphPaneSlot::getValue).toList();
        gui.graphPane.clear();
        nodeWidgetMap.clear();
        Group dummy2 = new Group();
        new Scene(dummy2);
        for (final var node : graphWrapper.getNodes()) {
            final var nodeWidget = new NodeWidget(node, this::updateGraphPaneContents);
            nodeWidget.setOnAction(actionEvent -> {
                if (actionMode == EActionMode.REMOVE) {
                    actionEvent.consume();
                    graphWrapper.removeNode(node);
                    updateGraphPaneContents();
                } else if (actionMode == EActionMode.EDGE_ADD) {
                    actionEvent.consume();
                    if (edgeBeingAdded) {
                        try {
                            if (edgeStartNode != null) {
                                graphWrapper.addEdge(edgeStartNode.value, node);
                            }
                        } catch (Exception ignore) {
                        }
                        edgeStartNode = null;
                        edgeBeingAdded = false;
                        updateGraphPaneContents();
                    } else {
                        edgeBeingAdded = true;
                        edgeStartNode = nodeWidget;
                    }
                }
            });
            nodeWidget.setOnNodeShapeChanged(nodeShape -> graphWrapper.setNodeShape(node, nodeShape));
            nodeWidget.setOnTextChanged(s -> graphWrapper.setNodeText(node, s));
            nodeWidgetMap.put(nodeWidget.value, nodeWidget);
            dummy2.getChildren().add(nodeWidget);
        }
        dummy2.layout();
        dummy2.applyCss();
        dummy2.layout();
        for (final var edge : graphWrapper.getEdges()) {
            final var edgeWidget = new EdgeWidget(edge, graphWrapper, this::updateGraphPaneContents, this);
            final var edgeSlot = gui.graphPane.addChild(edgeWidget);
            edgeSlot.setDraggable(false);
            for (var pointWidget : edgeWidget.getPathPoints()) {
                final var pointSlot = gui.graphPane.addChild(pointWidget);
                oldSelection.stream().filter(pointWidget::equals).findFirst().ifPresent(node -> gui.graphPane.selectAlso(pointSlot));
                if (pointWidget.i != -1) {
                    pointSlot.setOnMoved(actionEvent -> {
                        graphWrapper.updatePointOnEdge(pointWidget.parentEdge, pointWidget.i, new Point2D(pointWidget.getLayoutX(), pointWidget.getLayoutY()));
                        updateGraphPaneContents();
                    });
                    pointWidget.setOnAction(actionEvent -> {
                        if (actionMode == EActionMode.REMOVE) {
                            actionEvent.consume();
                            graphWrapper.removePointFromEdge(edge, pointWidget.i);
                            updateGraphPaneContents();
                        }
                    });
                } else {
                    pointSlot.setDraggable(false);
                }
            }
        }
        for (var nodeWidget : nodeWidgetMap.values()) {
            final var nodeSlot = gui.graphPane.addChild(nodeWidget);
            oldSelection.stream().filter(nodeWidget::equals).findFirst().ifPresent(node -> gui.graphPane.selectAlso(nodeSlot));
            nodeSlot.setOnMoved(actionEvent -> {
                graphWrapper.setNodeTranslate(nodeWidget.value, nodeWidget.getLayoutX(), nodeWidget.getLayoutY());
                updateGraphPaneContents();
            });
        }
    }
}
