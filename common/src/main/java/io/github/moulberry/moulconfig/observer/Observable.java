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

import java.util.function.Function;

/**
 * An observable value can be accessed and can dispatch updates about state changes to interested parties
 *
 * @param <T> the type of the value
 */
public interface Observable<T> {
    /**
     * @return the current state of the value
     */
    T get();

    /**
     * Create a new Observable which will always have the value of {@code mapper.apply(get())} and reports on updates accordingly.
     * Note that even if the mapper maps two different values to the same new value, an update will still be dispatched.
     *
     * @param mapper a function to transform the value
     * @param <V>    the type of the new value
     * @return a new {@link Observable}
     */
    default <V> Observable<V> map(Function<T, V> mapper) {
        return new MapObservable<>(this, mapper);
    }

    /**
     * Add a new observer which is interested in updates about this value
     *
     * @param observer the observer which is interested
     */
    void addObserver(Observer<T> observer);

    /**
     * Alias for {@link #addObserver}.
     *
     * @param observer the observer which is interested
     * @see #addObserver
     */
    default void whenChanged(Observer<T> observer) {
        addObserver(observer);
    }
}
