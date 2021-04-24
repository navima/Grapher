package grapher;// CHECKSTYLE:OFF

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class GUI {
    final @NotNull Controller controller;

    public GUI(@NotNull Controller controller) {
        this.controller = controller;
        controller.gui = this;
    }

    final GraphPane graphPane = new GraphPane();
    @NotNull Scene show(@NotNull Stage stage) {
        final var root = new Group();
        final Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add("style.css");
        stage.setTitle("Grapher");
        // Graph visualizer -------------------------------------------------------------------
        controller.updateGraphPaneContents();
        graphPane.getStyleClass().add("menubar");
        graphPane.prefWidthProperty().bind(scene.widthProperty());
        graphPane.prefHeightProperty().bind(scene.heightProperty());

        graphPane.setOnMouseClicked(controller::graphPaneOnMouseClicked);

        root.getChildren().add(graphPane);
        // Menu strip -------------------------------------------------------------------------
        MenuBar bar = new MenuBar();
        // File menu ----------------------------------------------------------------
        Menu filemenu = new Menu("File");
        MenuItem fNew = new MenuItem("New");
        fNew.setOnAction(controller::fileMenuNewFileHandler);
        MenuItem fSave = new MenuItem("Save");
        fSave.setOnAction(controller::fileMenuSaveHandler);
        MenuItem fSaveAs = new MenuItem("Save As...");
        fSaveAs.setOnAction(controller::fileMenuSaveAsHandler);
        MenuItem fLoad = new MenuItem("Load...");
        fLoad.setOnAction(controller::fileMenuLoadHandler);
        filemenu.getItems().addAll(fNew, fSave,fSaveAs, fLoad);
        bar.getMenus().add(filemenu);
        // --------------------------------------------------------------------------
        bar.getStyleClass().add("menubar");
        root.getChildren().add(bar);
        // Toolbar ----------------------------------------------------------------------------
        VBox toolbar = new VBox();
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.prefHeightProperty().bind(scene.heightProperty());
        toolbar.setPickOnBounds(false);
        toolbar.getStyleClass().add("menubar");



        
        var bPan = new Button("Pan");
        bPan.setOnAction(controller::panHandler);
        var bAddN = new Button("Add Node");
        bAddN.setOnAction(controller::addNodeHandler);
        var bAddE = new Button("Add Edge");
        bAddE.setOnAction(controller::addEdgeHandler);
        var bRemove = new Button("Remove");
        bRemove.setOnAction(controller::removeNodeEdgeHandler);

        toolbar.getChildren().addAll(bPan,bAddN,bAddE,bRemove);
        root.getChildren().add(toolbar);
        // ------------------------------------------------------------------------------------


        return scene;
    }
}
