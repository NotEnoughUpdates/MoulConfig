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
 * An observable that wraps another observable and applies a mapper function to its content.
 *
 * @param <T> the type of the wrapped observable
 * @param <V> the type of the mapped observable
 * @see Observable#map
 */
public class MapObservable<T, V> extends BaseObservable<V> implements Observer<T> {
    V value;
    Observable<T> root;
    Function<T, V> mapper;

    MapObservable(Observable<T> root, Function<T, V> mapper) {
        this.root = root;
        this.mapper = mapper;
        value = mapper.apply(root.get());
        root.addObserver(this);
    }

    @Override
    public V get() {
        return value;
    }

    @Override
    public void observeChange(T oldT, T newT) {
        V oldV = this.value;
        this.value = mapper.apply(newT);
        notifyObservers(oldV, this.value);
    }
}
