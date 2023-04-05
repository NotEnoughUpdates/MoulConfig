package io.github.moulberry.moulconfig.observer;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A generic class holding a value. May be backed directly by a field, or a property.
 */
public interface GetSetter<T> extends Supplier<T>, Consumer<T> {
    /**
     * Get the value. Subsequent evaluations may return different values.
     */
    @Override
    T get();

    /**
     * Set the value.
     */
    void set(T newValue);

    @Override
    default void accept(T t) {
        set(t);
    }
}

