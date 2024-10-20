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

package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;

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
        return child.getWidth();
    }

    @Override
    public int getHeight() {
        return child.getHeight();
    }

    GuiImmediateContext getChildContext(GuiImmediateContext context) {
        return context.translated(
            getChildOffsetX(context),
            getChildOffsetY(context),
            Math.min(child.getWidth(), context.getWidth()),
            Math.min(child.getHeight(), context.getHeight())
        );
    }

    public int getChildOffsetX(GuiImmediateContext context) {
        if (child.getWidth() > context.getWidth()) return 0;
        return context.getWidth() / 2 - child.getWidth() / 2;
    }

    public int getChildOffsetY(GuiImmediateContext context) {
        if (child.getHeight() > context.getHeight()) return 0;
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
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        return child.keyboardEvent(event, getChildContext(context));
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        return child.mouseEvent(mouseEvent, getChildContext(context));
    }
}
