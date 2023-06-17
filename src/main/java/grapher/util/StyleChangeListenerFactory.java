package grapher.util;

import javafx.collections.ListChangeListener;
import javafx.css.Styleable;

public class StyleChangeListenerFactory {
    /**
     * Creates listener that propagates Add and Remove changes to args.
     * @param args Styleables to propagate to
     */
    public static ListChangeListener<? super String> copierListener(Styleable... args) {
        return change -> {
            change.next();
            if (change.wasRemoved())
                for (var arg : args)
                    arg.getStyleClass().removeAll(change.getRemoved());
            else if (change.wasAdded())
                for (var arg : args)
                    arg.getStyleClass().addAll(change.getAddedSubList());
        };
    }
}
