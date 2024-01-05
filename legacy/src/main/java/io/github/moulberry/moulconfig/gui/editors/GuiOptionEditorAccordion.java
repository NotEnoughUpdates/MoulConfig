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

package io.github.moulberry.moulconfig.gui.editors;

import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.internal.RenderUtils;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

public class GuiOptionEditorAccordion extends GuiOptionEditor {
    private final int accordionId;
    private boolean accordionToggled = false;

    public GuiOptionEditorAccordion(ProcessedOption option, int accordionId) {
        super(option);
        this.accordionId = accordionId;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    public int getAccordionId() {
        return accordionId;
    }

    public boolean getToggled() {
        return accordionToggled;
    }

    @Override
    public void render(int x, int y, int width) {
        int height = getHeight();
        RenderUtils.drawFloatingRectDark(x, y, width, height, true);

        RenderUtils.drawOpenCloseTriangle(accordionToggled, x + 6, y + 6, 13.5 - 6, 13.5 - 6);

        TextRenderUtils.drawStringScaledMaxWidth(option.name, Minecraft.getMinecraft().fontRendererObj,
            x + 18, y + 6, false, width - 10, 0xc0c0c0
        );
    }

    @Override
    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        if (Mouse.getEventButtonState() && mouseX > x && mouseX < x + width &&
            mouseY > y && mouseY < y + getHeight()) {
            accordionToggled = !accordionToggled;
            return true;
        }

        return false;
    }

    @Override
    public boolean keyboardInput() {
        return false;
    }
}
