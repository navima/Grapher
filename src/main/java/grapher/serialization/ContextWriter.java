package grapher.serialization;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.StringWriter;

@AllArgsConstructor
public class ContextWriter extends StringWriter {
    @Getter
    private final Object context;
}
