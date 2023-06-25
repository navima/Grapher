package grapher;// CHECKSTYLE:OFF

import grapher.widget.GraphPane;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class GUI {
    final @NotNull Controller controller;
    public ToggleButton bPan;
    public ToggleButton bAddN;
    public ToggleButton bAddE;
    public ToggleButton bRemove;
    GraphPane currentGraphPane;
    private TabPane tabPane;
    private Object graphTabMarker = "graphTab";

    public GUI(@NotNull Controller controller) {
        this.controller = controller;
        controller.gui = this;
    }

    @NotNull Scene show(@NotNull Stage stage) {
        final var root = new Group();

        final var scene = new Scene(root, 640, 480);
        scene.getStylesheets().add("style.css");
        stage.setTitle("Grapher");


        tabPane = new TabPane();
        Tab spacerTab = new Tab();
        spacerTab.setDisable(true);
        spacerTab.setClosable(false);
        Rectangle spacer = new Rectangle();
        spacerTab.setGraphic(spacer);
        tabPane.getTabs().add(spacerTab);

        tabPane.getTabs().addAll(controller.manipulators.stream().map(this::generateTabAndGraphPane).toList());

        Tab addGraphTab = new Tab();
        addGraphTab.setGraphic(new Label("+"));
        addGraphTab.setClosable(false);
        tabPane.getTabs().add(addGraphTab);
        addGraphTab.getGraphic().setOnMouseClicked(e -> {
            GraphManipulator manipulator = controller.addGraphManipulator();
            Tab tab = generateTabAndGraphPane(manipulator);
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
            e.consume();
        });

        tabPane.getSelectionModel().select(1);

        controller.updateGraphPaneContents();

        MenuBar menuBar = generateMenuBar();
        spacer.widthProperty().bind(menuBar.widthProperty());
        tabPane.prefWidthProperty().bind(scene.widthProperty());
        tabPane.prefHeightProperty().bind(scene.heightProperty());
        root.getChildren().addAll(tabPane, menuBar, generateToolbar(scene));

        return scene;
    }

    public void updateGraphTabs() {
        tabPane.getTabs().removeIf(tab -> tab.getUserData() == graphTabMarker);
        tabPane.getTabs().addAll(1, controller.manipulators.stream().map(this::generateTabAndGraphPane).toList());
    }

    @NotNull
    private Tab generateTabAndGraphPane(GraphManipulator manipulator) {
        var gp = new GraphPane();
        var tab = new Tab("", gp);
        var headerLabel = new Label();
        headerLabel.setMinWidth(15);
        var renameTextField = new TextField(manipulator.graph.name);
        renameTextField.maxHeightProperty().bind(headerLabel.heightProperty());
        // Set Max and Min Width to PREF_SIZE so that the TextField is always PREF
        renameTextField.setMinWidth(Region.USE_PREF_SIZE);
        renameTextField.setMaxWidth(Region.USE_PREF_SIZE);
        renameTextField.textProperty().addListener((ov, prevText, currText) -> {
            // Do this in a Platform.runLater because of Textfield has no padding at first time and so on
            Platform.runLater(() -> {
                Text text = new Text(currText);
                text.setFont(renameTextField.getFont()); // Set the same font, so the size is the same
                double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                        + renameTextField.getPadding().getLeft() + renameTextField.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing
                renameTextField.setPrefWidth(width); // Set the width
                renameTextField.positionCaret(renameTextField.getCaretPosition()); // If you remove this line, it flashes a little bit
            });
        });
        renameTextField.focusedProperty().addListener((observableValue, oldFocus, newFocus) -> {
            if (!newFocus) {
                tab.setGraphic(headerLabel);
                controller.graphNameChangeHandler(renameTextField.getText());
            }
        });
        headerLabel.textProperty().bind(renameTextField.textProperty());
        headerLabel.setOnMouseClicked(e -> {
            if (e.isStillSincePress()
                    && e.getClickCount() > 1) {
                tab.setGraphic(renameTextField);
                renameTextField.requestFocus();
                e.consume();
            }
        });
        tab.setGraphic(headerLabel);
        tab.setClosable(false);
        setUpGraphPane(gp);
        tab.setOnSelectionChanged(e -> {
            currentGraphPane = gp;
            controller.changeActiveGraph(manipulator);
            e.consume();
        });
        tab.setUserData("graphTab");
        return tab;
    }

    private void setUpGraphPane(GraphPane graphPane) {
        graphPane.getStyleClass().add("menubar");
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
        bar.setUseSystemMenuBar(true);
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
        var fProjectSettings = new MenuItem("_Project Settings");
        fProjectSettings.setOnAction(controller::fileMenuProjectSettingsHandler);
        var fExport = new MenuItem("_Export...");
        fExport.setOnAction(controller::fileMenuExportHandler);
        var fLoad = new MenuItem("_Load...");
        fLoad.setOnAction(controller::fileMenuLoadHandler);
        fLoad.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));

        filemenu.getItems().addAll(fNew, fSave, fSaveAs, fProjectSettings, fExport, fLoad);
        return filemenu;
    }
}
