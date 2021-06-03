package grapher;// CHECKSTYLE:OFF

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

    /**
     * Default constructor.
     * @param controller The controller handling our events.
     */
    public GUI(@NotNull Controller controller) {
        this.controller = controller;
        controller.gui = this;
    }

    VBox toolbar;
    public ToggleGroup toolboxGroup;
    public ToggleButton bPan;
    public ToggleButton bAddN;
    public ToggleButton bAddE;
    public ToggleButton bRemove;
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
        Menu filemenu = new Menu("_File");
        MenuItem fNew = new MenuItem("_New");
        fNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        fNew.setOnAction(controller::fileMenuNewFileHandler);
        MenuItem fSave = new MenuItem("_Save");
        fSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        fSave.setOnAction(controller::fileMenuSaveHandler);
        MenuItem fSaveAs = new MenuItem("Sa_ve As...");
        fSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        fSaveAs.setOnAction(controller::fileMenuSaveAsHandler);
        MenuItem fLoad = new MenuItem("_Load...");
        fLoad.setOnAction(controller::fileMenuLoadHandler);
        fLoad.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));

        filemenu.getItems().addAll(fNew, fSave,fSaveAs, fLoad);
        bar.getMenus().add(filemenu);
        // Edit menu ----------------------------------------------------------------
        Menu editmenu = new Menu("_Edit");

        MenuItem eUndo = new MenuItem("_Undo");
        eUndo.setOnAction(controller::undoHandler);
        eUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        MenuItem eRedo = new MenuItem("Re_do");
        eRedo.setOnAction(controller::redoHandler);
        eRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        MenuItem ePan = new MenuItem("_Pan");
        ePan.setOnAction(controller::panHandler);
        ePan.setAccelerator(new KeyCodeCombination(KeyCode.W));
        MenuItem eAddNode = new MenuItem("_Add Node");
        eAddNode.setOnAction(controller::addNodeHandler);
        eAddNode.setAccelerator(new KeyCodeCombination(KeyCode.A));
        MenuItem eAddEdge = new MenuItem("Add E_dge");
        eAddEdge.setOnAction(controller::addEdgeHandler);
        eAddEdge.setAccelerator(new KeyCodeCombination(KeyCode.E));
        MenuItem eRemove = new MenuItem("_Remove");
        eRemove.setOnAction(controller::removeNodeEdgeHandler);
        eRemove.setAccelerator(new KeyCodeCombination(KeyCode.X));

        editmenu.getItems().addAll(eUndo, eRedo, ePan, eAddNode, eAddEdge, eRemove);
        bar.getMenus().add(editmenu);
        // --------------------------------------------------------------------------
        bar.getStyleClass().add("menubar");
        root.getChildren().add(bar);
        // Toolbar ----------------------------------------------------------------------------
        toolbar = new VBox();
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.prefHeightProperty().bind(scene.heightProperty());
        toolbar.setPickOnBounds(false);
        toolbar.getStyleClass().add("menubar");




        toolboxGroup = new ToggleGroup();

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

        toolbar.getChildren().addAll(bPan,bAddN,bAddE,bRemove);
        root.getChildren().add(toolbar);
        // ------------------------------------------------------------------------------------


        return scene;
    }
}
