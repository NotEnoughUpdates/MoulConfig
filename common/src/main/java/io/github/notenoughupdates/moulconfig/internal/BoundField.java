package io.github.notenoughupdates.moulconfig.internal;

import java.lang.reflect.Field;
import java.util.Objects;

public final class BoundField {
    private final Field field;
    private final Object boundTo;

    public BoundField(Field field, Object boundTo) {
        this.field = field;
        this.boundTo = boundTo;
    }

    public Field getField() {
        return field;
    }

    public Object getBoundTo() {
        return boundTo;
    }

    public Field component1() {
        return field;
    }

    public Object component2() {
        return boundTo;
    }

    public BoundField copy(Field field, Object boundTo) {
        return new BoundField(field, boundTo);
    }

    @Override
    public String toString() {
        return field + " bound to " + boundTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoundField)) return false;
        BoundField that = (BoundField) o;
        return Objects.equals(field, that.field) && Objects.equals(boundTo, that.boundTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, boundTo);
    }
}
