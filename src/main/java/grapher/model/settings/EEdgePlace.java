package grapher.model.settings;

public enum EEdgePlace {
    SEPARATE("separate"),
    EMBEDDED_IN_FROM_NODE("embedded in the 'from' node");

    final String value;

    EEdgePlace(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
