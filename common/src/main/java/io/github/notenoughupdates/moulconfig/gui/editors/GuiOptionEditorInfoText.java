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

package io.github.notenoughupdates.moulconfig.gui.editors;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.component.TextComponent;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GuiOptionEditorInfoText extends ComponentEditor {
    private String infoTitle;
    GuiComponent component;

    public GuiOptionEditorInfoText(ProcessedOption option, String infoTitle) {
        super(option);

        this.infoTitle = infoTitle;
        if (this.infoTitle != null && this.infoTitle.isEmpty()) this.infoTitle = null;
    }

    @Override
    public @NotNull GuiComponent getDelegate() {
        if (component == null)
            component = wrapComponent(new TextComponent(infoTitle, 100, TextComponent.TextAlignment.CENTER));
        return component;
    }

    @Override
    public boolean fulfillsSearch(String word) {
        return super.fulfillsSearch(word) || (infoTitle != null && infoTitle.toLowerCase(Locale.ROOT).contains(word));
    }
}
