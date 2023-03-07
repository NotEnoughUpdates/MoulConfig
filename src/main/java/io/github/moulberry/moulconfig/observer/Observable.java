package io.github.moulberry.moulconfig.observer;

import java.util.function.Function;

public interface Observable<T> {
    T get();

    default <V> Observable<V> map(Function<T, V> mapper) {
        return new MapObservable<>(this, mapper);
    }

    void addObserver(Observer<T> observer);

    default void whenChanged(Observer<T> observer) {
        addObserver(observer);
    }
}
