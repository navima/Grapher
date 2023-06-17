package grapher.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
public class Project {
    public Settings settings = new Settings();
    public List<Graph> graphs = new ArrayList<>();
}
