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

package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import lombok.ToString;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * A gui element composing multiple other gui elements by stacking them horizontally.
 */
@ToString
public class RowComponent extends GuiComponent {
    final List<GuiComponent> children;

    public RowComponent(List<GuiComponent> children) {
        this.children = children;
    }

    public RowComponent(GuiComponent... children) {
        this(Arrays.asList(children));
    }

    @Override
    public int getWidth() {
        return foldChildren(0, (child, width) -> child.getWidth() + width);
    }

    @Override
    public int getHeight() {
        return foldChildren(0, (child, height) -> Math.max(child.getHeight(), height));
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        for (GuiComponent child : children) {
            initial = visitor.apply(child, initial);
        }
        return initial;
    }

    public void foldWithContext(GuiImmediateContext context, BiConsumer<GuiComponent, GuiImmediateContext> visitor) {
        foldChildren(0, (child, position) -> {
            visitor.accept(child, context.translated(position, 0, child.getWidth(), child.getHeight()));
            return child.getWidth() + position;
        });
    }

    @Override
    public void render(GuiImmediateContext context) {
        GlStateManager.pushMatrix();
        foldWithContext(context, (child, childContext) -> {
            child.render(childContext);
            GlStateManager.translate(child.getWidth(), 0, 0);
        });
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        foldWithContext(context, GuiComponent::mouseEvent);
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        foldWithContext(context, GuiComponent::keyboardEvent);
    }

}
