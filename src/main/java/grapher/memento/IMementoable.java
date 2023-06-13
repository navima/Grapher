package grapher.memento;

public interface IMementoable<T> {
    IMemento<T> getState();
}
