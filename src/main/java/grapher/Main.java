package grapher;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * The main class of the program.
 */
public class Main extends Application {
    private static GUI gui;

    public static void main(String[] args) {
        Controller controller = new Controller(new ObjectMapper());
        gui = new GUI(controller);
        launch();
    }

    @Override
    public void start(@NotNull Stage stage) {
        stage.setScene(gui.show(stage));
        stage.show();
    }
}
