package grapher;// CHECKSTYLE:OFF

import grapher.widget.GraphPane;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class GUI {
    final @NotNull Controller controller;

    public GUI(@NotNull Controller controller) {
        this.controller = controller;
        controller.gui = this;
    }

    public ToggleButton bPan;
    public ToggleButton bAddN;
    public ToggleButton bAddE;
    public ToggleButton bRemove;
    final GraphPane graphPane = new GraphPane();

    @NotNull Scene show(@NotNull Stage stage) {
        final var root = new Group();
        
        final var scene = new Scene(root, 640, 480);
        scene.getStylesheets().add("style.css");
        stage.setTitle("Grapher");

        controller.updateGraphPaneContents();
        setUpGraphPane(scene);

        root.getChildren().addAll(graphPane, generateMenuBar(), generateToolbar(scene));

        return scene;
    }

    private void setUpGraphPane(Scene scene) {
        graphPane.getStyleClass().add("menubar");
        graphPane.prefWidthProperty().bind(scene.widthProperty());
        graphPane.prefHeightProperty().bind(scene.heightProperty());
        graphPane.setOnMouseClicked(controller::graphPaneOnMouseClicked);
    }

    @NotNull
    private VBox generateToolbar(Scene scene) {
        // Toolbar ----------------------------------------------------------------------------
        VBox toolbar = new VBox();
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.prefHeightProperty().bind(scene.heightProperty());
        toolbar.setPickOnBounds(false);
        toolbar.getStyleClass().add("menubar");


        var toolboxGroup = new ToggleGroup();

        bPan = new ToggleButton("Pan");
        bPan.setToggleGroup(toolboxGroup);
        bPan.setOnAction(controller::panHandler);
        bAddN = new ToggleButton("Add Node");
        bAddN.setToggleGroup(toolboxGroup);
        bAddN.setOnAction(controller::addNodeHandler);
        bAddE = new ToggleButton("Add Edge");
        bAddE.setToggleGroup(toolboxGroup);
        bAddE.setOnAction(controller::addEdgeHandler);
        bRemove = new ToggleButton("Remove");
        bRemove.setToggleGroup(toolboxGroup);
        bRemove.setOnAction(controller::removeNodeEdgeHandler);

        toolbar.getChildren().addAll(bPan, bAddN, bAddE, bRemove);
        return toolbar;
    }

    @NotNull
    private MenuBar generateMenuBar() {
        // Menu strip -------------------------------------------------------------------------
        final var bar = new MenuBar();
        bar.getMenus().addAll(generateFileMenu(), generateEditMenu());
        bar.getStyleClass().add("menubar");
        return bar;
    }

    @NotNull
    private Menu generateEditMenu() {
        // Edit menu ----------------------------------------------------------------
        var editmenu = new Menu("_Edit");

        var eUndo = new MenuItem("_Undo");
        eUndo.setOnAction(controller::undoHandler);
        eUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        var eRedo = new MenuItem("Re_do");
        eRedo.setOnAction(controller::redoHandler);
        eRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        var ePan = new MenuItem("_Pan");
        ePan.setOnAction(controller::panHandler);
        ePan.setAccelerator(new KeyCodeCombination(KeyCode.W));
        var eAddNode = new MenuItem("_Add Node");
        eAddNode.setOnAction(controller::addNodeHandler);
        eAddNode.setAccelerator(new KeyCodeCombination(KeyCode.A));
        var eAddEdge = new MenuItem("Add E_dge");
        eAddEdge.setOnAction(controller::addEdgeHandler);
        eAddEdge.setAccelerator(new KeyCodeCombination(KeyCode.E));
        var eRemove = new MenuItem("_Remove");
        eRemove.setOnAction(controller::removeNodeEdgeHandler);
        eRemove.setAccelerator(new KeyCodeCombination(KeyCode.X));

        editmenu.getItems().addAll(eUndo, eRedo, ePan, eAddNode, eAddEdge, eRemove);
        return editmenu;
    }

    @NotNull
    private Menu generateFileMenu() {
        // File menu ----------------------------------------------------------------
        var filemenu = new Menu("_File");
        var fNew = new MenuItem("_New");
        fNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        fNew.setOnAction(controller::fileMenuNewFileHandler);
        var fSave = new MenuItem("_Save");
        fSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        fSave.setOnAction(controller::fileMenuSaveHandler);
        var fSaveAs = new MenuItem("Sa_ve As...");
        fSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        fSaveAs.setOnAction(controller::fileMenuSaveAsHandler);
        var fLoad = new MenuItem("_Load...");
        fLoad.setOnAction(controller::fileMenuLoadHandler);
        fLoad.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));

        filemenu.getItems().addAll(fNew, fSave, fSaveAs, fLoad);
        return filemenu;
    }
}
