import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class GUI {
    UserController controller;

    public GUI(UserController controller) {
        this.controller = controller;
    }

    File showFilePrompt(Stage stage, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select save location");
        return fileChooser.showOpenDialog(stage);
    }

    eActionMode actionMode = eActionMode.PAN;
    NodeWidget edgeStartNode = null;
    boolean edgeBeingAdded = false;

    final GraphPane graphPane = new GraphPane();
    void updateGraphPaneContents() {
        graphPane.clear();
        for (final var edge : controller.graph.edges.entrySet()) {
            var temp = new EdgeWidget(edge.getKey(), edge.getValue(), controller, this::updateGraphPaneContents, this);
            graphPane.addChild(temp);
        }
        for (final var node : controller.graph.nodes.entrySet()) {
            var temp = new NodeWidget(node.getValue(), node.getKey(), controller, this::updateGraphPaneContents,this);
            graphPane.addChild(temp);
        }
    }


    Scene show(Stage stage) {
        final var root = new Group();
        final Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add("style.css");

        // Graph visualizer -------------------------------------------------------------------
        updateGraphPaneContents();
        graphPane.getStyleClass().add("menubar");
        graphPane.setMinSize(640,480);
        graphPane.setMaxSize(640,480);

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
        });

        root.getChildren().add(graphPane);
        // Menu strip -------------------------------------------------------------------------
        MenuBar bar = new MenuBar();
        // File menu ----------------------------------------------------------------
        Menu filemenu = new Menu("File");
        MenuItem fSave = new MenuItem("Save");
        fSave.setOnAction(actionEvent -> {
            if(! controller.save())
                controller.save(showFilePrompt(stage, "Save To"));
        });
        MenuItem fLoad = new MenuItem("Load...");
        fLoad.setOnAction(actionEvent -> {
            if(! controller.load())
                controller.load(showFilePrompt(stage, "Load From"));
        });
        filemenu.getItems().addAll(fSave, fLoad);
        bar.getMenus().add(filemenu);
        // --------------------------------------------------------------------------
        bar.getStyleClass().add("menubar");
        root.getChildren().add(bar);
        // ------------------------------------------------------------------------------------
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
