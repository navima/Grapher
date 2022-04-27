package grapher.memento;

import java.util.List;

public interface ICaretaker {
    List<GraphMemento> getHistory();
    void restoreState(GraphMemento memento);
    void captureState();
    void undo();
    void redo();
}
