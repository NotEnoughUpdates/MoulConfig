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

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for an {@link Observable} which takes care of the observer saving
 */
public abstract class BaseObservable<T> implements Observable<T> {
    /**
     * Set of observers that are subscribed to this state changes of this observable.
     */
    protected Set<Observer<T>> observers = new HashSet<>();

    @Override
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    /**
     * Notifies observers about a change of state
     *
     * @param oldT the old value to report to the observers
     * @param newT the new value to report to the observers
     */
    protected void notifyObservers(T oldT, T newT) {
        for (Observer<T> observer : observers) {
            observer.observeChange(oldT, newT);
        }
    }
}
