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
 */
package io.github.notenoughupdates.moulconfig.gui;

import io.github.notenoughupdates.moulconfig.common.RenderContext;

import java.util.Objects;

/**
 * A context containing the constraints of a gui elements, as well as the state of the user interface, relative to that gui element.
 */
public final class GuiImmediateContext {
    private final RenderContext renderContext;
    private final int renderOffsetX;
    private final int renderOffsetY;
    private final int width;
    private final int height;
    private final int mouseX;
    private final int mouseY;
    private final int absoluteMouseX;
    private final int absoluteMouseY;
    private final float mouseXHF;
    private final float mouseYHF;

    public GuiImmediateContext(
        RenderContext renderContext,
        int renderOffsetX,
        int renderOffsetY,
        int width,
        int height,
        int mouseX,
        int mouseY,
        int absoluteMouseX,
        int absoluteMouseY,
        float mouseXHF,
        float mouseYHF
    ) {
        this.renderContext = renderContext;
        this.renderOffsetX = renderOffsetX;
        this.renderOffsetY = renderOffsetY;
        this.width = width;
        this.height = height;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.absoluteMouseX = absoluteMouseX;
        this.absoluteMouseY = absoluteMouseY;
        this.mouseXHF = mouseXHF;
        this.mouseYHF = mouseYHF;
    }

    public RenderContext getRenderContext() {
        return renderContext;
    }

    public int getRenderOffsetX() {
        return renderOffsetX;
    }

    public int getRenderOffsetY() {
        return renderOffsetY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getAbsoluteMouseX() {
        return absoluteMouseX;
    }

    public int getAbsoluteMouseY() {
        return absoluteMouseY;
    }

    public float getMouseXHF() {
        return mouseXHF;
    }

    public float getMouseYHF() {
        return mouseYHF;
    }

    /**
     * Check if the mouse is positioned within this context.
     */
    public boolean isHovered() {
        return mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height;
    }

    public GuiImmediateContext withBleed(int xBleed, int yBleed) {
        return new GuiImmediateContext(
            renderContext,
            renderOffsetX - xBleed,
            renderOffsetY - yBleed,
            width + 2 * xBleed,
            height + 2 * yBleed,
            mouseX + xBleed,
            mouseY + yBleed,
            absoluteMouseX,
            absoluteMouseY,
            mouseXHF,
            mouseYHF
        );
    }

    public GuiImmediateContext translated(int xOffset, int yOffset, int width, int height) {
        return new GuiImmediateContext(
            renderContext,
            renderOffsetX + xOffset,
            renderOffsetY + yOffset,
            width,
            height,
            mouseX - xOffset,
            mouseY - yOffset,
            absoluteMouseX,
            absoluteMouseY,
            mouseXHF - xOffset,
            mouseYHF - yOffset
        );
    }

    public GuiImmediateContext translatedNonRendering(int xOffset, int yOffset, int width, int height) {
        return new GuiImmediateContext(
            renderContext,
            renderOffsetX,
            renderOffsetY,
            width,
            height,
            mouseX - xOffset,
            mouseY - yOffset,
            absoluteMouseX,
            absoluteMouseY,
            mouseXHF,
            mouseYHF
        );
    }

    public GuiImmediateContext limitSize(int maxWidth, int maxHeight) {
        return translated(0, 0, Math.min(width, maxWidth), Math.min(height, maxHeight));
    }

    public GuiImmediateContext scaled(float scale) {
        return new GuiImmediateContext(
            renderContext,
            renderOffsetX,
            renderOffsetY,
            (int) (width / scale),
            (int) (height / scale),
            (int) ((mouseX - renderOffsetX) * scale),
            (int) ((mouseY - renderOffsetY) * scale),
            absoluteMouseX,
            absoluteMouseY,
            (mouseXHF - renderOffsetX) * scale,
            (mouseYHF - renderOffsetY) * scale
        );
    }

    public GuiImmediateContext copy(
        RenderContext renderContext,
        int renderOffsetX,
        int renderOffsetY,
        int width,
        int height,
        int mouseX,
        int mouseY,
        int absoluteMouseX,
        int absoluteMouseY,
        float mouseXHF,
        float mouseYHF
    ) {
        return new GuiImmediateContext(renderContext, renderOffsetX, renderOffsetY, width, height, mouseX, mouseY, absoluteMouseX, absoluteMouseY, mouseXHF, mouseYHF);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuiImmediateContext)) return false;
        GuiImmediateContext that = (GuiImmediateContext) o;
        return renderOffsetX == that.renderOffsetX
            && renderOffsetY == that.renderOffsetY
            && width == that.width
            && height == that.height
            && mouseX == that.mouseX
            && mouseY == that.mouseY
            && absoluteMouseX == that.absoluteMouseX
            && absoluteMouseY == that.absoluteMouseY
            && Float.compare(that.mouseXHF, mouseXHF) == 0
            && Float.compare(that.mouseYHF, mouseYHF) == 0
            && Objects.equals(renderContext, that.renderContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderContext, renderOffsetX, renderOffsetY, width, height, mouseX, mouseY, absoluteMouseX, absoluteMouseY, mouseXHF, mouseYHF);
    }

    @Override
    public String toString() {
        return "GuiImmediateContext(renderContext=" + renderContext
            + ", renderOffsetX=" + renderOffsetX
            + ", renderOffsetY=" + renderOffsetY
            + ", width=" + width
            + ", height=" + height
            + ", mouseX=" + mouseX
            + ", mouseY=" + mouseY
            + ", absoluteMouseX=" + absoluteMouseX
            + ", absoluteMouseY=" + absoluteMouseY
            + ", mouseXHF=" + mouseXHF
            + ", mouseYHF=" + mouseYHF + ")";
    }
}
