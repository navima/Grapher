package grapher.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
public class Project {
    public Settings settings = new Settings();
    public List<Graph> graphs = new ArrayList<>();
    @JsonIgnore
    public File source;
}
