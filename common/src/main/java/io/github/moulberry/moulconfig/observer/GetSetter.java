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

    @Override
    default void accept(T t) {
        set(t);
    }

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
}

