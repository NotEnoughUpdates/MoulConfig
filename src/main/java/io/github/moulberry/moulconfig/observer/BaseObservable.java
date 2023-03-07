package io.github.moulberry.moulconfig.observer;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseObservable<T> implements Observable<T> {
    protected Set<Observer<T>> observers = new HashSet<>();

    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    protected void notifyObservers(T oldT, T newT) {
        for (Observer<T> observer : observers) {
            observer.observeChange(oldT, newT);
        }
    }
}
