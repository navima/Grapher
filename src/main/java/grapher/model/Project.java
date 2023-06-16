package grapher.model;

import lombok.Data;

import java.util.List;

@Data
public class Project {
    public Settings settings;
    public List<Graph> graphs;
}
