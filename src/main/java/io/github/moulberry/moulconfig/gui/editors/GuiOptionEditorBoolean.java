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

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.gui.GuiOptionEditor;
import io.github.moulberry.moulconfig.gui.elements.GuiElementBoolean;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.gui.DrawContext;

public class GuiOptionEditorBoolean extends GuiOptionEditor {

    private final GuiElementBoolean bool;
    private final Config config;
    private final int runnableId;

    public GuiOptionEditorBoolean(
        ProcessedOption option,
        int runnableId,
        Config config
    ) {
        super(option);
        this.config = config;
        this.runnableId = runnableId;
        bool = new GuiElementBoolean(0, 0, () -> (boolean) option.get(), 10, (value) -> onUpdate(option, value));
    }

    @Override
    public void render(DrawContext context, int x, int y, int width) {
        super.render(context, x, y, width);
        int height = getHeight();

        bool.x = x + width / 6 - 24;
        bool.y = y + height - 7 - 14;
        bool.render(context, 0, 0, 0);
    }

    @Override
    public boolean mouseInput(int x, int y, int width, double mouseX, double mouseY, int button) {
        int height = getHeight();
        bool.x = x + width / 6 - 24;
        bool.y = y + height - 7 - 14;
        return bool.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(int x, int y, int width, double mouseX, double mouseY, int button) {
        int height = getHeight();
        bool.x = x + width / 6 - 24;
        bool.y = y + height - 7 - 14;
        return bool.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyboardInput(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    private void onUpdate(ProcessedOption option, boolean value) {
        if (option.set(value)) {
            config.executeRunnable(runnableId);
        }
    }
}
