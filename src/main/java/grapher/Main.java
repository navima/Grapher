package grapher;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * The main class of the program.
 */
public class Main extends Application {
    private static GUI gui;

    public static void main(String[] args) {
        IGraph graphWrapper = new GraphManipulator();
        graphWrapper.loadDefault();
        Controller controller = new Controller(graphWrapper);
        gui = new GUI(controller);
        launch();
    }

    @Override
    public void start(@NotNull Stage stage) {
        stage.setScene(gui.show(stage));
        stage.show();
    }
}
