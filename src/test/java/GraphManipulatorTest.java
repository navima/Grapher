import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import grapher.GraphManipulator;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class GraphManipulatorTest {

    @org.junit.jupiter.api.Test
    void saveVoid() {
        var t = new GraphManipulator();
        try {
            assertFalse(t.save());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void saveFile() {
        var t = new GraphManipulator();
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
        var t = new GraphManipulator();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            assertFalse(t.load((File) null));
            // wrong document type test
            t.reset();
            File file = new File(classLoader.getResource("invalid.json").getFile());
            File finalFile = file;
            assertThrows(JsonParseException.class,
                    () -> t.load(finalFile));

            // wrong object type test
            t.reset();
            file = new File(classLoader.getResource("incorrect.json").getFile());
            File finalFile1 = file;
            assertThrows(JsonMappingException.class,
                    () -> t.load(finalFile1));

            // read own save
            file = new File("test.json");
            var t2 = new GraphManipulator();
            t2.loadDefault();
            var t3 = new GraphManipulator();
            assertTrue(t2.save(file));
            assertTrue(t3.load(file));
            assertEquals(t3, t2);

            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void loadURL() {
        var t = new GraphManipulator();
        assertThrows(IOException.class, () -> t.load(new URL("file:/asd")));
    }

    @org.junit.jupiter.api.Test
    void addEdge() {

    }

    @org.junit.jupiter.api.Test
    void reset() {
        var t1 = new GraphManipulator();
        var t2 = new GraphManipulator();
        t2.loadDefault();
        t2.reset();
        assertEquals(t2, t1);
    }
}