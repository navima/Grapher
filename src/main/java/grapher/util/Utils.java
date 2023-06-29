package grapher.util;

public class Utils {
    public static <T> T transformNullIf(T value, boolean condition, T defaultValue) {
        return (value == null && condition) ? defaultValue : value;
    }

    public static String transformNullIf(String value, boolean condition) {
        return transformNullIf(value, condition, "");
    }
}
