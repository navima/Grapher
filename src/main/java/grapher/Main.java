package grapher;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * The main class of the program.
 */
public class Main extends Application {
    static IGraph graphWrapper;
    static Controller controller;
    static GUI gui;

    /**
     * The main entry point of the program.
     * @param args args
     */
    public static void main(String[] args) {
        graphWrapper = new GraphWrapper();
        graphWrapper.loadDefault();
        controller = new Controller(graphWrapper);
        gui = new GUI(controller);
        launch();
    }

    @Override
    public void start(@NotNull Stage stage) {
        stage.setScene(gui.show(stage));
        stage.show();
    }
}
