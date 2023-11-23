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

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A generic class holding a value. May be backed directly by a field, or a property.
 */
public interface GetSetter<T> extends Supplier<T>, Consumer<T> {
    /**
     * Get the value. Subsequent evaluations may return different values.
     */
    @Override
    T get();

    /**
     * Set the value.
     */
    void set(T newValue);

    /**
     * Bridge method for {@link Consumer}. Equivalent to {@link #set}
     */
    @Override
    default void accept(T t) {
        set(t);
    }

    /**
     * Create a constant {@link GetSetter}. This {@link GetSetter} will always throw when {@link #set} is called.
     *
     * @param t   the value that this {@link GetSetter} will always return
     * @param <T> the type of the value this {@link GetSetter} will hold
     * @return a constant {@link GetSetter}
     */
    static <T> GetSetter<T> constant(T t) {
        return new GetSetter<T>() {
            @Override
            public T get() {
                return t;
            }

            @Override
            public void set(T newValue) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Create a floating {@link GetSetter}. This {@link GetSetter} is not backed by anything and will simply act like a normal field.
     *
     * @param t   the initial value
     * @param <T> the type of the value this {@link GetSetter} will hold
     * @return a floating {@link GetSetter}
     */
    static <T> GetSetter<T> floating(T t) {
        return new GetSetter<T>() {
            T storage = t;

            @Override
            public T get() {
                return storage;
            }

            @Override
            public void set(T newValue) {
                storage = newValue;
            }
        };
    }
}

