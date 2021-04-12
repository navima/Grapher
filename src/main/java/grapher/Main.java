package grapher;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main class of the program.
 */
public class Main extends Application {
    static GraphWrapper controller;
    static GUI gui;

    /**
     * The main entry point of the program.
     * @param args args
     */
    public static void main(String[] args) {
        controller = new GraphWrapper(true);
        gui = new GUI(controller);
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(gui.show(stage));
        stage.show();
    }
}
