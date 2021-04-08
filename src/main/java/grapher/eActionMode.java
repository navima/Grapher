package grapher;

/**
 * The type of action to perform.
 */
public enum eActionMode {
    /**
     * User is panning the canvas.
     */
    PAN,
    /**
     * User is adding node.
     */
    NODE_ADD,
    /**
     * User is adding edge.
     */
    EDGE_ADD,
    /**
     * User is removing node/edge.
     */
    REMOVE,
}
