package grapher;// CHECKSTYLE:OFF

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import grapher.dialog.ProjectSettingsDialogPanel;
import grapher.model.Edge;
import grapher.model.Graph;
import grapher.model.Node;
import grapher.model.Project;
import grapher.serialization.CustomEdgeSerializer;
import grapher.serialization.CustomNodeSerializer;
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
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling user actions.
 */
public class Controller {
    /**
     * The manipulated graph.
     */
    private GraphManipulator currentManipulator;
    public final List<GraphManipulator> manipulators = new ArrayList<>();
    public Project project;
    /**
     * The GUI we are handling the events of.
     */
    public GUI gui; //TODO: Loosen the coupling.
    private final ObjectMapper mapper;

    public Controller(ObjectMapper mapper) {
        this.mapper = mapper;
        project = new Project();
        currentManipulator = addGraphManipulator();
        try {
            currentManipulator.graph = mapper.readValue(getClass().getClassLoader().getResource("default.json"), Graph.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearProject() {
        currentManipulator = null;
        manipulators.clear();
        project = null;
    }

    private void loadProject(Project project) {
        clearProject();
        this.project = project;
        for (var graph : project.graphs) {
            var manipulator = addGraphManipulator();
            manipulator.graph = graph;
        }
        currentManipulator = manipulators.get(0);
        gui.updateGraphTabs();
        updateGraphPaneContents();
    }

    public void graphPaneOnMouseClicked(@NotNull MouseEvent e) {
        if (actionMode == EActionMode.NODE_ADD) {
            if (e.isStillSincePress()) { // don't
                try {
                    var invTrans = gui.currentGraphPane.g.getLocalToParentTransform().createInverse();
                    var res = invTrans.transform(e.getX(), e.getY());
                    currentManipulator.addNode(
                            res.getX() - gui.currentGraphPane.getChildTranslateX(),
                            res.getY() - gui.currentGraphPane.getChildTranslateY());
                    updateGraphPaneContents();

                } catch (NonInvertibleTransformException nonInvertibleTransformException) {
                    Logger.error(nonInvertibleTransformException);
                }
            }
        }
        gui.currentGraphPane.requestFocus();
    }

    public void fileMenuNewFileHandler(ActionEvent actionEvent) {
        currentManipulator.reset();
        updateGraphPaneContents();
    }

    public void fileMenuSaveHandler(ActionEvent actionEvent) {
        try {
            if (project.source != null) {
                updateGraphs();
                mapper.writeValue(project.source, project);
            } else
                fileMenuSaveAsHandler(actionEvent);
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }

    public void fileMenuSaveAsHandler(ActionEvent actionEvent) {
        try {
            File file = showFilePrompt("Save Project As", EFileActionType.SAVE);
            if (file == null) return;
            project.source = file;
            updateGraphs();
            mapper.writeValue(file, project);
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }

    public void fileMenuLoadHandler(ActionEvent actionEvent) {
        try {
            File file = showFilePrompt("Load Project", EFileActionType.LOAD);
            if (file == null) return;
            project = mapper.readValue(file, Project.class);
            project.source = file;
            loadProject(project);
            Logger.info("Loaded project: {}", project);
            updateGraphPaneContents();
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }

    private void updateGraphs() {
        project.graphs = manipulators.stream().map(graphManipulator -> graphManipulator.graph).toList();
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
        currentManipulator.undo();
        updateGraphPaneContents();
    }

    public void redoHandler(ActionEvent actionEvent) {
        currentManipulator.redo();
        updateGraphPaneContents();
    }

    public GraphManipulator addGraphManipulator() {
        GraphManipulator manipulator = new GraphManipulator(manipulators.size());
        manipulators.add(manipulator);
        return manipulator;
    }

    public void changeActiveGraph(GraphManipulator manipulator) {
        currentManipulator = manipulator;
        updateGraphPaneContents();
    }

    public void graphNameChangeHandler(String text) {
        currentManipulator.setGraphName(text);
    }

    public void fileMenuProjectSettingsHandler(ActionEvent actionEvent) {
        new ProjectSettingsDialogPanel(project.settings).show();
    }

    /**
     * Exports graphs next to save location.
     */
    public void fileMenuExportHandler(ActionEvent actionEvent) {
        if (project.source == null) {
            new Alert(Alert.AlertType.ERROR, "Can't export before saving at least once.").showAndWait();
            return;
        }
        ObjectMapper exportMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Node.class, new CustomNodeSerializer(project.settings));
        simpleModule.addSerializer(Edge.class, new CustomEdgeSerializer(project.settings));
        exportMapper.registerModule(simpleModule);
        try {
            if (project.settings.separateFilesForGraphs) {
                // save into multiple files
                for (var graph : project.graphs) {
                    exportMapper.writeValue(appendToProjectFilePath(graph.name + ".json"), graph);
                }
            } else {
                // save into one file
                exportMapper.writeValue(appendToProjectFilePath("export.json"), project);
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.toString()).showAndWait();
        }
    }

    @NotNull
    private File appendToProjectFilePath(String... s) {
        return new File(project.source.getParentFile(), FilenameUtils.getBaseName(project.source.getName()) + "-" + String.join("-", s));
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
        var oldSelection = gui.currentGraphPane.getSelection().stream().map(GraphPane.GraphPaneSlot::getValue).toList();
        gui.currentGraphPane.clear();
        nodeWidgetMap.clear();
        Group dummy2 = new Group();
        new Scene(dummy2);
        for (final var node : currentManipulator.getNodes()) {
            final var nodeWidget = new NodeWidget(node, this::updateGraphPaneContents);
            nodeWidget.setOnAction(actionEvent -> {
                if (actionMode == EActionMode.REMOVE) {
                    actionEvent.consume();
                    currentManipulator.removeNode(node);
                    updateGraphPaneContents();
                } else if (actionMode == EActionMode.EDGE_ADD) {
                    actionEvent.consume();
                    if (edgeBeingAdded) {
                        try {
                            if (edgeStartNode != null) {
                                currentManipulator.addEdge(edgeStartNode.value, node);
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
            nodeWidget.setOnNodeShapeChanged(nodeShape -> currentManipulator.setNodeShape(node, nodeShape));
            nodeWidget.setOnTextChanged(s -> currentManipulator.setNodeText(node, s));
            nodeWidgetMap.put(nodeWidget.value, nodeWidget);
            dummy2.getChildren().add(nodeWidget);
        }
        dummy2.layout();
        dummy2.applyCss();
        dummy2.layout();
        for (final var edge : currentManipulator.getEdges()) {
            final var edgeWidget = new EdgeWidget(edge, currentManipulator, this::updateGraphPaneContents, this);
            final var edgeSlot = gui.currentGraphPane.addChild(edgeWidget);
            edgeSlot.setDraggable(false);
            for (var pointWidget : edgeWidget.getPathPoints()) {
                final var pointSlot = gui.currentGraphPane.addChild(pointWidget);
                oldSelection.stream().filter(pointWidget::equals).findFirst().ifPresent(node -> gui.currentGraphPane.selectAlso(pointSlot));
                if (pointWidget.i != -1) {
                    pointSlot.setOnMoved(actionEvent -> {
                        currentManipulator.updatePointOnEdge(pointWidget.parentEdge, pointWidget.i, new Point2D(pointWidget.getLayoutX(), pointWidget.getLayoutY()));
                        updateGraphPaneContents();
                    });
                    pointWidget.setOnAction(actionEvent -> {
                        if (actionMode == EActionMode.REMOVE) {
                            actionEvent.consume();
                            currentManipulator.removePointFromEdge(edge, pointWidget.i);
                            updateGraphPaneContents();
                        }
                    });
                } else {
                    pointSlot.setDraggable(false);
                }
            }
        }
        for (var nodeWidget : nodeWidgetMap.values()) {
            final var nodeSlot = gui.currentGraphPane.addChild(nodeWidget);
            oldSelection.stream().filter(nodeWidget::equals).findFirst().ifPresent(node -> gui.currentGraphPane.selectAlso(nodeSlot));
            nodeSlot.setOnMoved(actionEvent -> {
                currentManipulator.setNodeTranslate(nodeWidget.value, nodeWidget.getLayoutX(), nodeWidget.getLayoutY());
                updateGraphPaneContents();
            });
        }
    }
}
