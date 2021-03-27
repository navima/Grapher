import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    static UserController controller;
    static GUI gui;

    public static void main(String[] args) {
        controller = new UserController();
        gui = new GUI(controller);
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(gui.show(stage));
        stage.show();
    }
}
