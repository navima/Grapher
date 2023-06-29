package grapher.model.settings;

import grapher.util.Section;
import grapher.util.Title;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Settings {
    @Section("Export Format")
    @Title("Generate separate file for each graph")
    public boolean separateFilesForGraphs = false;
    @Title("Where to store edges")
    public EEdgePlace eEdgePlace = EEdgePlace.SEPARATE;
    @Title("Transform null strings to default value?")
    public boolean transformNull = false;
    public String fromNodeEdgeFieldName = "edges";
    public String nodeIdName = "id";
    public String nodeTextName = "text";
    public boolean writeNodeX = true;
    public boolean writeNodeY = true;
    public boolean writeNodeShape = true;
    public String edgeToName = "to";
    public String edgeTextName = "text";
    public boolean writeEdgeId = true;
    public boolean writeEdgeFrom = true;
    public boolean writeEdgeTo = true;
    public boolean writeEdgePoints = true;
    public boolean writeGraphName = true;
    @Title("Make the root json object an array, containing only the nodes")
    public boolean collapseGraphIntoNodes = false;
}
