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

package io.github.moulberry.moulconfig;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class Config {
    public void executeRunnable(int runnableId) {
    }

    public List<Social> getSocials() {
        return new ArrayList<>();
    }

    public Text getTitle() {
        return Text.literal("Config GUI");
    }

    public void saveNow() {
    }

    public boolean shouldAutoFocusSearchbar() {
        return false;
    }

    public boolean shouldSearchCategoryNames() {
        return true;
    }
}
