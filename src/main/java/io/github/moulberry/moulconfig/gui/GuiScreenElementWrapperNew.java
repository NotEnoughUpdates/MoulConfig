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

package io.github.moulberry.moulconfig.gui;

import lombok.NonNull;
import lombok.ToString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;


@ToString
public class GuiScreenElementWrapperNew extends Screen {
    @NonNull
    public GuiContext context;

    public GuiScreenElementWrapperNew(@NotNull GuiContext guiContext) {
        super(Text.of("Gui screen element wrapper new"));
        this.context = guiContext;
    }

    int lastMouseX = 0, lastMouseY = 0;


    GuiImmediateContext createContext() {
        int x = (int) MinecraftClient.getInstance().mouse.getX() * MinecraftClient.getInstance().getWindow().getScaledWidth() / MinecraftClient.getInstance().getWindow().getWidth();
        int y = (int) MinecraftClient.getInstance().mouse.getY() * MinecraftClient.getInstance().getWindow().getScaledHeight() / MinecraftClient.getInstance().getWindow().getHeight();
        return new GuiImmediateContext(
                width, height,
                x, y, x, y
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        context.getRoot().keyboardEvent(createContext());
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);;
        this.context.getRoot().render(context, new GuiImmediateContext(width, height, mouseX, mouseY, mouseX, mouseY));
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseMoved(mouseX, mouseY);
        this.lastMouseX = (int) mouseX;
        this.lastMouseY = (int) mouseY;
        context.getRoot().mouseEvent(button, createContext());

        return true;
    }
}
