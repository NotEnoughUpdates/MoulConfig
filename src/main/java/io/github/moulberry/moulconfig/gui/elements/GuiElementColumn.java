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

package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import lombok.ToString;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * A gui element composing multiple other gui elements by stacking them vertically.
 */
@ToString
public class GuiElementColumn extends GuiElementNew {
    final List<GuiElementNew> children;

    public GuiElementColumn(List<GuiElementNew> children) {
        this.children = children;
    }

    public GuiElementColumn(GuiElementNew... children) {
        this(Arrays.asList(children));
    }

    @Override
    public int getWidth() {
        return foldChildren(0, (child, width) -> Math.max(child.getWidth(), width));
    }

    @Override
    public int getHeight() {
        return foldChildren(0, (child, height) -> child.getHeight() + height);
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiElementNew, T, T> visitor) {
        for (GuiElementNew child : children) {
            initial = visitor.apply(child, initial);
        }
        return initial;
    }

    public void foldWithContext(GuiImmediateContext context, BiConsumer<GuiElementNew, GuiImmediateContext> visitor) {
        foldChildren(0, (child, position) -> {
            visitor.accept(child, context.translated(0, position, child.getWidth(), child.getHeight()));
            return child.getHeight() + position;
        });
    }

    @Override
    public void render(DrawContext drawContext, GuiImmediateContext context) {
        drawContext.getMatrices().push();
        foldWithContext(context, (child, childContext) -> {
            child.render(drawContext, childContext);
            drawContext.getMatrices().translate(0, child.getHeight(), 0);
        });
        drawContext.getMatrices().pop();
    }

    @Override
    public void mouseEvent(int button, GuiImmediateContext context) {
        foldWithContext(context, (guiElementNew, guiImmediateContext) -> {
            guiElementNew.mouseEvent(button, guiImmediateContext);
        });
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        foldWithContext(context, GuiElementNew::keyboardEvent);
    }
}
