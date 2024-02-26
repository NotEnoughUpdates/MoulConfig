package io.github.notenoughupdates.moulconfig.observer;

import com.google.gson.annotations.Expose;

class PropertyImpl<T> extends Property<T> {
    @Expose
    T value;

    PropertyImpl(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T newValue) {
        T oldValue = this.value;
        this.value = newValue;
        notifyObservers(oldValue, newValue);
    }

}
