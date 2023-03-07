package io.github.moulberry.moulconfig.observer;

import java.util.function.Function;

public class MapObservable<T, V> extends BaseObservable<V> implements Observer<T> {
    V value;
    Observable<T> root;
    Function<T, V> mapper;

    public MapObservable(Observable<T> root, Function<T, V> mapper) {
        this.root = root;
        this.mapper = mapper;
        value = mapper.apply(root.get());
        root.addObserver(this);
    }

    @Override
    public V get() {
        return value;
    }

    @Override
    public void observeChange(T oldT, T newT) {
        V oldV = this.value;
        this.value = mapper.apply(newT);
        notifyObservers(oldV, this.value);
    }
}
