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
