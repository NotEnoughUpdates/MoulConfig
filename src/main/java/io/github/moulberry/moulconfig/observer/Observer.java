package io.github.moulberry.moulconfig.observer;

public interface Observer<T> {
    void observeChange(T oldValue, T newValue);
}
