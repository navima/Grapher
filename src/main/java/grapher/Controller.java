package grapher;

import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Controller {
    final GraphWrapper graphWrapper;
    public GUI gui;

    public Controller(GraphWrapper graphWrapper) {
        this.graphWrapper = graphWrapper;
    }
    public void graphPaneOnMouseClicked(@NotNull MouseEvent e) {
        if (actionMode == eActionMode.NODE_ADD) {
            if (!gui.graphPane.isPanning) { // don't
                graphWrapper.addNode(
                        e.getX() - gui.graphPane.getChildTranslateX(),
                        e.getY() - gui.graphPane.getChildTranslateY());
                updateGraphPaneContents();
            }
        }
        gui.graphPane.isPanning = false;    // I'm sorry, God.
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
                graphWrapper.save(showFilePrompt("Save To", eFileActionType.SAVE));
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }
    public void fileMenuSaveAsHandler(ActionEvent actionEvent) {
        try {
            graphWrapper.save(showFilePrompt("Save To", eFileActionType.SAVE));
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }
    public void fileMenuLoadHandler(ActionEvent actionEvent) {
        try {
            var res = graphWrapper.load(showFilePrompt("Load From", eFileActionType.LOAD));
            if (res)
                updateGraphPaneContents();
        } catch (IOException e) {
            e.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }
    public void panHandler(ActionEvent e) {
        actionMode = eActionMode.PAN;
    }
    public void addNodeHandler(ActionEvent e) {
        actionMode = eActionMode.NODE_ADD;
    }
    public void addEdgeHandler(ActionEvent e) {
        actionMode = eActionMode.EDGE_ADD;
    }
    public void removeNodeEdgeHandler(ActionEvent e) {
        actionMode = eActionMode.REMOVE;
    }
    public enum eFileActionType{
        SAVE, LOAD
    }
    public @Nullable File showFilePrompt(String title, @NotNull eFileActionType type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Json Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setTitle(title);
        switch (type){
            case LOAD:
                return fileChooser.showOpenDialog(new Stage());
            case SAVE:
                return fileChooser.showSaveDialog(new Stage());
        }
        return null;
    }

    @NotNull eActionMode actionMode = eActionMode.PAN;
    @Nullable NodeWidget edgeStartNode = null;
    boolean edgeBeingAdded = false;

    final HashMap<Integer, NodeWidget> nodeWidgetMap = new HashMap<>();
    void updateGraphPaneContents() {
        gui.graphPane.clear();
        nodeWidgetMap.clear();
        Group dummy2 = new Group();
        Scene dummy = new Scene(dummy2);
        for (final var node : graphWrapper.graph.nodes.entrySet()) {
            var temp = new NodeWidget(node.getValue(), node.getKey(), this::updateGraphPaneContents);
            temp.setOnAction( actionEvent -> {
                var id = node.getKey();
                if (actionMode == eActionMode.REMOVE) {
                    actionEvent.consume();
                    graphWrapper.removeNode(id);
                    updateGraphPaneContents();
                } else if (actionMode == eActionMode.EDGE_ADD) {
                    actionEvent.consume();
                    if (edgeBeingAdded) {
                        try {
                            graphWrapper.addEdge(edgeStartNode.id, id);
                        } catch (Exception ignore) { }
                        edgeStartNode = null;
                        edgeBeingAdded = false;
                        updateGraphPaneContents();
                    } else {
                        edgeBeingAdded = true;
                        edgeStartNode = temp;
                    }
                }
            });
            temp.setOnDragEnded((aDouble, aDouble2) -> graphWrapper.setNodeTranslate(node.getKey(), aDouble, aDouble2));
            temp.setOnNodeShapeChanged(nodeShape -> graphWrapper.setNodeShape(node.getKey(), nodeShape));
            temp.setOnTextChanged(s -> graphWrapper.setNodeText(node.getKey(), s));
            nodeWidgetMap.put(temp.id, temp);
            dummy2.getChildren().add(temp);
        }
        dummy2.layout();
        dummy2.applyCss();
        dummy2.layout();
        for (final var edge : graphWrapper.graph.edges.entrySet()) {
            var temp = new EdgeWidget(edge.getKey(), edge.getValue(), graphWrapper, this::updateGraphPaneContents, this);
            gui.graphPane.addChild(temp);
        }
        for (var child : nodeWidgetMap.values()){
            gui.graphPane.addChild(child);
        }
    }
}
