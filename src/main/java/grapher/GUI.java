package grapher;// CHECKSTYLE:OFF

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class GUI {
    final UserController controller;

    public GUI(UserController controller) {
        this.controller = controller;
    }

    private enum eFileActionType{
        SAVE, LOAD
    }
    File showFilePrompt(Stage stage, String title, eFileActionType type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Json Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setTitle(title);
        switch (type){
            case LOAD:
                return fileChooser.showOpenDialog(stage);
            case SAVE:
                return fileChooser.showSaveDialog(stage);
        }
        return null;
    }

    eActionMode actionMode = eActionMode.PAN;
    NodeWidget edgeStartNode = null;
    boolean edgeBeingAdded = false;

    final HashMap<Integer, NodeWidget> nodeWidgetMap = new HashMap<>();
    final GraphPane graphPane = new GraphPane();
    void updateGraphPaneContents() {
        graphPane.clear();
        nodeWidgetMap.clear();
        Group dummy2 = new Group();
        Scene dummy = new Scene(dummy2);
        for (final var node : controller.graph.nodes.entrySet()) {
            var temp = new NodeWidget(node.getValue(), node.getKey(), controller, this::updateGraphPaneContents, this);
            nodeWidgetMap.put(temp.id, temp);
            dummy2.getChildren().add(temp);
        }
        dummy2.layout();
        dummy2.applyCss();
        dummy2.layout();
        for (final var edge : controller.graph.edges.entrySet()) {
            var temp = new EdgeWidget(edge.getKey(), edge.getValue(), controller, this::updateGraphPaneContents, this);
            graphPane.addChild(temp);
        }
        for (var child : nodeWidgetMap.values()){
            graphPane.addChild(child);
        }
    }


    Scene show(Stage stage) {
        final var root = new Group();
        final Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add("style.css");
        stage.setTitle("Grapher");
        // Graph visualizer -------------------------------------------------------------------
        updateGraphPaneContents();
        graphPane.getStyleClass().add("menubar");
        graphPane.prefWidthProperty().bind(scene.widthProperty());
        graphPane.prefHeightProperty().bind(scene.heightProperty());

        graphPane.setOnMouseClicked(e -> {
            if (actionMode == eActionMode.NODE_ADD) {
                if (!graphPane.isPanning) { // don't
                    controller.addNode(
                            e.getX()-graphPane.getChildTranslateX(),
                            e.getY()-graphPane.getChildTranslateY());
                    updateGraphPaneContents();
                }
            }
            graphPane.isPanning = false;    // I'm sorry, God.
            graphPane.requestFocus();
        });

        root.getChildren().add(graphPane);
        // Menu strip -------------------------------------------------------------------------
        MenuBar bar = new MenuBar();
        // File menu ----------------------------------------------------------------
        Menu filemenu = new Menu("File");
        MenuItem fNew = new MenuItem("New");
        fNew.setOnAction(actionEvent -> {
            controller.reset();
            updateGraphPaneContents();
        });
        MenuItem fSave = new MenuItem("Save");
        fSave.setOnAction(actionEvent -> {
            try {
                var res = controller.save();
                if(! res)
                    controller.save(showFilePrompt(stage, "Save To",eFileActionType.SAVE));
            } catch (IOException e) {
                e.printStackTrace();
                var alert = new Alert(Alert.AlertType.ERROR,e.toString());
                alert.showAndWait();
            }
        });
        MenuItem fSaveAs = new MenuItem("Save As...");
        fSaveAs.setOnAction(actionEvent -> {
            try {
                 controller.save(showFilePrompt(stage, "Save To",eFileActionType.SAVE));
            } catch (IOException e) {
                e.printStackTrace();
                var alert = new Alert(Alert.AlertType.ERROR,e.toString());
                alert.showAndWait();
            }
        });
        MenuItem fLoad = new MenuItem("Load...");
        fLoad.setOnAction(actionEvent -> {
            try {
                var res = controller.load(showFilePrompt(stage, "Load From",eFileActionType.LOAD));
                if(res)
                    updateGraphPaneContents();
            } catch (IOException e) {
                e.printStackTrace();
                var alert = new Alert(Alert.AlertType.ERROR,e.toString());
                alert.showAndWait();
            }
        });
        filemenu.getItems().addAll(fNew, fSave,fSaveAs, fLoad);
        bar.getMenus().add(filemenu);
        // --------------------------------------------------------------------------
        bar.getStyleClass().add("menubar");
        root.getChildren().add(bar);
        // Toolbar ----------------------------------------------------------------------------
        VBox toolbar = new VBox();
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setMinHeight(480);
        toolbar.setMaxHeight(480);
        toolbar.setPickOnBounds(false);
        toolbar.getStyleClass().add("menubar");



        
        var bPan = new Button("Pan");
        bPan.setOnAction(e -> actionMode=eActionMode.PAN);
        var bAddN = new Button("Add Node");
        bAddN.setOnAction(e -> actionMode=eActionMode.NODE_ADD);
        var bAddE = new Button("Add Edge");
        bAddE.setOnAction(e -> actionMode=eActionMode.EDGE_ADD);
        var bRemove = new Button("Remove");
        bRemove.setOnAction(e -> actionMode=eActionMode.REMOVE);

        toolbar.getChildren().addAll(bPan,bAddN,bAddE,bRemove);
        root.getChildren().add(toolbar);
        // ------------------------------------------------------------------------------------


        return scene;
    }
}
