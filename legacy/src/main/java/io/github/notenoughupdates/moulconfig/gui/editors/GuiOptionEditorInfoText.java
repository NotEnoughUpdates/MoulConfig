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

import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.internal.TextRenderUtils;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Locale;

public class GuiOptionEditorInfoText extends GuiOptionEditor {
    private String infoTitle;

    public GuiOptionEditorInfoText(ProcessedOption option, String infoTitle) {
        super(option);

        this.infoTitle = infoTitle;
        if (this.infoTitle != null && this.infoTitle.isEmpty()) this.infoTitle = null;
    }

    @Override
    public void render(int x, int y, int width) {
        super.render(x, y, width);

        int height = getHeight();

        GlStateManager.color(1, 1, 1, 1);

        if (infoTitle != null) {
            TextRenderUtils.drawStringCenteredScaledMaxWidth(infoTitle, Minecraft.getMinecraft().fontRendererObj,
                x + width / 6, y + height - 7 - 6,
                false, 44, 0xFF303030
            );
        }
    }

    @Override
    public boolean fulfillsSearch(String word) {
        return super.fulfillsSearch(word) || (infoTitle != null && infoTitle.toLowerCase(Locale.ROOT).contains(word));
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        return false;
    }

    @Override
    public boolean keyboardInput() {
        return false;
    }
}
