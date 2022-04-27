package grapher.memento;

public interface IMementoable<T> {
    IMemento<T> getState();
    //void restoreState(IMemento<T> memento);
}
