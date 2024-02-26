package io.github.notenoughupdates.moulconfig.observer;

class PropertyUpgraded<T> extends Property<T> {
    private final GetSetter<T> getSetter;

    public PropertyUpgraded(GetSetter<T> getSetter) {
        this.getSetter = getSetter;
    }

    @Override
    public void set(T newValue) {
        T old = get();
        getSetter.set(newValue);
        notifyObservers(old, get());
    }

    @Override
    public T get() {
        return getSetter.get();
    }
}
