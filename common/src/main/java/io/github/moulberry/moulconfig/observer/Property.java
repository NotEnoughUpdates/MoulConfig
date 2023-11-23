/*
 * Copyright (C) 2023 NotEnoughUpdates contributors
 *
 * This file is part of MoulConfig.
 *
 * MoulConfig is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * MoulConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MoulConfig. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.github.moulberry.moulconfig.observer;

import com.google.gson.annotations.Expose;

/**
 * A property is a variable which is modifiable and can have those mutations observed.
 * When serializing a property to Json you can use {@link PropertyTypeAdapterFactory} to automatically
 * serialize the stored value in place, instead of wrapping the value in a dictionary.
 * <p>
 * Whenever a config option calls for a field with type {@code T}, then a {@code Property<T>} will also work.
 *
 * @param <T> the type of value this Property can hold.
 */
public class Property<T> extends BaseObservable<T> implements GetSetter<T> {
    /**
     * The internal storage of this property
     */
    @Expose
    T value;

    private Property(T value) {
        this.value = value;
    }

    /**
     * Create a Property with a given initial value.
     *
     * @param value the initial value of the property
     * @param <T>   the type of the value
     * @return a newly constructed property
     */
    public static <T> Property<T> of(T value) {
        return new Property<>(value);
    }

    @Override
    public T get() {
        return value;
    }

    /**
     * Explicitly notify observers about state changes that are internal to the stored value.
     */
    public void notifyObservers() {
        notifyObservers(value, value);
    }

    @Override
    public void set(T newValue) {
        T oldValue = this.value;
        this.value = newValue;
        notifyObservers(oldValue, newValue);
    }

}
