package grapher;

/**
 * Wrapper for grapher.Main, needed for Maven Assembly plugin.
 */
public class HyperMain {
    /**
     * The main entry point of the program.
     *
     * @param args args
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
