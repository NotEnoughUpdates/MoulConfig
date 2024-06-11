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

import io.github.notenoughupdates.moulconfig.common.NinePatches;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import lombok.Getter;

import java.util.function.BiFunction;

/**
 * Renders an element with a floating rect.
 */
@Getter
public class PanelComponent extends GuiComponent {

    public interface BackgroundRenderer {
        void render(RenderContext renderContext, int x, int y, int width, int height);
    }

    public enum DefaultBackgroundRenderer implements BackgroundRenderer {
        DARK_RECT {
            @Override
            public void render(RenderContext renderContext, int x, int y, int width, int height) {
                renderContext.drawDarkRect(x, y, width, height);
            }
        },
        BUTTON {
            @Override
            public void render(RenderContext renderContext, int x, int y, int width, int height) {
                renderContext.drawNinePatch(NinePatches.INSTANCE.createButton(), x, y, width, height);
            }
        },
        VANILLA {
            @Override
            public void render(RenderContext renderContext, int x, int y, int width, int height) {
                renderContext.drawNinePatch(NinePatches.INSTANCE.createVanillaPanel(), x, y, width, height);
            }
        },
        TRANSPARENT {
            @Override
            public void render(RenderContext renderContext, int x, int y, int width, int height) {
            }
        }
    }

    private final GuiComponent element;
    private final int insets;
    private final BackgroundRenderer backgroundRenderer;

    /**
     * @param element            the child element to render the panels contents
     * @param insets             the padding size of this panel
     * @param backgroundRenderer the renderer to render the background of this panel
     * @see DefaultBackgroundRenderer
     */
    public PanelComponent(GuiComponent element, int insets, BackgroundRenderer backgroundRenderer) {
        this.element = element;
        this.insets = insets;
        this.backgroundRenderer = backgroundRenderer;
    }

    public PanelComponent(GuiComponent element) {
        this(element, 2, DefaultBackgroundRenderer.DARK_RECT);
    }

    @Override
    public <T> T foldChildren(T initial, BiFunction<GuiComponent, T, T> visitor) {
        return visitor.apply(element, initial);
    }

    @Override
    public int getWidth() {
        return element.getWidth() + insets * 2;
    }

    @Override
    public int getHeight() {
        return element.getHeight() + insets * 2 + 2;
    }

    protected GuiImmediateContext getChildContext(GuiImmediateContext context) {
        return context.translated(insets, insets, context.getWidth() - insets * 2, context.getHeight() - insets * 2 - 2);
    }

    @Override
    public void render(GuiImmediateContext context) {
        context.getRenderContext().pushMatrix();
        backgroundRenderer.render(context.getRenderContext(), 0, 0, context.getWidth(), context.getHeight() - 2);
        context.getRenderContext().translate(insets, insets, 0);
        element.render(getChildContext(context));
        context.getRenderContext().popMatrix();
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        return element.keyboardEvent(event, getChildContext(context));
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        return element.mouseEvent(mouseEvent, getChildContext(context));
    }
}
