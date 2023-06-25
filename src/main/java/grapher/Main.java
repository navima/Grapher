package grapher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import grapher.model.Edge;
import grapher.model.Graph;
import grapher.model.Node;
import grapher.serialization.*;
import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * The main class of the program.
 */
public class Main extends Application {
    private static GUI gui;

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(new SimpleModule()
                .addSerializer(Node.class, new NodeSerializer())
                .addSerializer(Edge.class, new EdgeSerializer())
                .addDeserializer(Graph.class, new GraphDeserializer()));
        Controller controller = new Controller(mapper);
        gui = new GUI(controller);
        launch();
    }

    @Override
    public void start(@NotNull Stage stage) {
        stage.setScene(gui.show(stage));
        stage.show();
    }
}
