package io.github.moulberry.moulconfig.observer;

public class Property<T> extends BaseObservable<T> {

    T value;

    private Property(T value) {
        this.value = value;
    }

    public static <T> Property<T> of(T value) {
        return new Property<>(value);
    }

    @Override
    public T get() {
        return value;
    }

    public void notifyObservers() {
        notifyObservers(value, value);
    }

    public void set(T newValue) {
        T oldValue = this.value;
        this.value = newValue;
        notifyObservers(oldValue, newValue);
    }

}
