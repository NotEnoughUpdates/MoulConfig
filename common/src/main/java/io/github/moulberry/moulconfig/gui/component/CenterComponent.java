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
import io.github.moulberry.moulconfig.gui.KeyboardEvent;
import io.github.moulberry.moulconfig.gui.MouseEvent;

import java.util.function.BiFunction;

/**
 * A gui element centers another gui element
 */
public class CenterComponent extends GuiComponent {
    final GuiComponent child;

    public CenterComponent(GuiComponent child) {
        this.child = child;
    }


    @Override
    public int getWidth() {
        return child.getHeight();
    }

    @Override
    public int getHeight() {
        return child.getHeight();
    }

    GuiImmediateContext getChildContext(GuiImmediateContext context) {
        return context.translated(
            getChildOffsetX(context),
            getChildOffsetY(context),
            child.getWidth(),
            child.getHeight()
        );
    }

    public int getChildOffsetX(GuiImmediateContext context) {
        return context.getWidth() / 2 - child.getWidth() / 2;
    }

    public int getChildOffsetY(GuiImmediateContext context) {
        return context.getHeight() / 2 - child.getHeight() / 2;
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(child, initial);
    }

    @Override
    public void render(GuiImmediateContext context) {
        context.getRenderContext().pushMatrix();
        context.getRenderContext().translate(getChildOffsetX(context), getChildOffsetY(context), 0);
        child.render(getChildContext(context));
        context.getRenderContext().popMatrix();
    }

    @Override
    public void keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        child.keyboardEvent(event, getChildContext(context));
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        child.mouseEvent(mouseEvent, getChildContext(context));
    }
}
