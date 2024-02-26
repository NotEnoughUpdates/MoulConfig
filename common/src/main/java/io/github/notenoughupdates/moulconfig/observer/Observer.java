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

package io.github.notenoughupdates.moulconfig.observer;

/**
 * Someone who is interested in getting notified about changes to an {@link Observable}
 *
 * @param <T> the type of variable in whose state changes we are interested
 */
@FunctionalInterface
public interface Observer<T> {
    /**
     * Called when the state is changed. Might be called with {@code oldValue == newValue}.
     * Will be called after the change is reflected in {@link Observable#get()}. May or may not be
     * called after the internal state of {@code oldValue} has been changed, in which case {@code oldValue}
     * may not have all the same information as it had after the last call (back when it was a {@code newValue}).
     *
     * @param oldValue the value before this change
     * @param newValue the value after this change
     */
    void observeChange(T oldValue, T newValue);

    /**
     * A method to indicate whether an observer is still valid. An invalid observer may be removed
     * from list of observers in an observables at some point.
     */
    default boolean isValid() {
        return true;
    }
}
