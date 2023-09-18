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
import io.github.moulberry.moulconfig.internal.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.BiFunction;

/**
 * Renders an element with a floating rect.
 */
public class PanelComponent extends GuiComponent {
    private final GuiComponent element;
    private final int insets;

    /**
     * @param element the child element to render the panels contents
     * @param insets  the padding size of this panel
     */
    public PanelComponent(GuiComponent element, int insets) {
        this.element = element;
        this.insets = insets;
    }

    public PanelComponent(GuiComponent element) {
        this(element, 2);
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(element, initial);
    }

    @Override
    public int getWidth() {
        return element.getWidth() + insets * 2;
    }

    public int getInsets() {
        return insets;
    }

    @Override
    public int getHeight() {
        return element.getHeight() + insets * 2;
    }

    GuiImmediateContext getChildContext(GuiImmediateContext context) {
        return context.translated(insets, insets, element.getWidth(), element.getHeight());
    }

    @Override
    public void render(GuiImmediateContext context) {
        GlStateManager.pushMatrix();
        RenderUtils.drawFloatingRectDark(0, 0, getWidth(), getHeight());
        GlStateManager.translate(insets, insets, 0);
        element.render(getChildContext(context));
        GlStateManager.popMatrix();
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        element.keyboardEvent(getChildContext(context));
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        element.mouseEvent(getChildContext(context));
    }
}
