package grapher.model.settings;

import grapher.util.Section;
import grapher.util.Title;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Settings {
    @Section("Dummy settings")
    @Title("This is a dummy text")
    public String dummyString;
    @Section("Export Format")
    @Title("Generate separate file for each graph")
    public boolean separateFilesForGraphs = false;
    @Title("Where to store edges")
    public EEdgePlace eEdgePlace = EEdgePlace.SEPARATE;
    public String fromNodeEdgeFieldName = "edges";
    public boolean writeNodeX = true;
    public boolean writeNodeY = true;
    public boolean writeNodeShape = true;
    public boolean writeEdgeId = true;
    public boolean writeEdgeFrom = true;
    public boolean writeEdgeTo = true;
    public boolean writeEdgePoints = true;
}
