import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @org.junit.jupiter.api.Test
    void saveVoid() {
        var t = new UserController();
        try {
            assertFalse(t.save());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void saveFile() {
        var t = new UserController();
        try {
            var file = new File("./test.json");
            assertTrue(t.save(file));
            assertTrue(t.save());
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assertFalse(t.save(null));
            assertFalse(t.save());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void loadFile() {
        var t = new UserController();
        try {
            assertFalse(t.load((File) null));
            File file = new File("./asd");

            // wrong document type test
            t.reset();
            try(FileWriter fw = new FileWriter(file)){
                fw.write("invalid json"); }
            assertThrows(JsonParseException.class,
                    () -> t.load(file));

            // wrong object type test
            t.reset();
            try(var fw2 = new FileWriter(file)){
                fw2.write("""
                    {"valid_json": "wrong_data"}"""); }
            assertThrows(JsonMappingException.class,
                    () -> t.load(file));

            // read own save
            var t2 = new UserController(true);
            var t3 = new UserController(false);
            assertTrue(t2.save(file));
            assertTrue(t3.load(file));
            System.out.println(t2);
            System.out.println(t3);
            assertEquals(t3, t2);


            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void loadURL() {
        var t = new UserController();
        assertThrows(IOException.class, () -> t.load(new URL("file:/asd")));
    }

    @org.junit.jupiter.api.Test
    void addEdge() {
        UserController t = new UserController();
        assertDoesNotThrow(() -> t.addEdge(0, 0));
        t.addNode(0,0);
        t.addNode(0,0);
        assertThrows(UserController.InvalidOperationException.class,
                () -> t.addEdge(9, -9));
        assertThrows(UserController.InvalidOperationException.class,
                () -> t.addEdge(0, -9));
        assertDoesNotThrow(
                () -> t.addEdge(0, 1));
        assertDoesNotThrow(
                () -> t.addEdge(0, 0));
    }

    @org.junit.jupiter.api.Test
    void reset() {
        var t1 = new UserController(false);
        var t2 = new UserController(true);
        t2.reset();
        assertEquals(t2, t1);
    }
}