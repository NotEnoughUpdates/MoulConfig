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

    /**
     * The current absolute offset for this gui context. This should not need to be accessed, unless you are contacting some API that does not access GlStateManager.
     */
    public int getRenderOffsetX() {
        return renderOffsetX;
    }

    /**
     * The current absolute offset for this gui context. This should not need to be accessed, unless you are contacting some API that does not access GlStateManager.
     */
    public int getRenderOffsetY() {
        return renderOffsetY;
    }

    /**
     * The available width for that gui element to render in.
     */
    public int getWidth() {
        return width;
    }

    /**
     * The available height for that gui element to render in.
     */
    public int getHeight() {
        return height;
    }

    /**
     * The position of the mouse, relative to this gui element.
     */
    public int getMouseX() {
        return mouseX;
    }

    /**
     * The position of the mouse, relative to this gui element.
     */
    public int getMouseY() {
        return mouseY;
    }

    /**
     * The position of the mouse, relative to the root element.
     */
    public int getAbsoluteMouseX() {
        return absoluteMouseX;
    }

    /**
     * The position of the mouse, relative to the root element.
     */
    public int getAbsoluteMouseY() {
        return absoluteMouseY;
    }

    /**
     * The position of the mouse, relative to this gui element in as high of a resolution as possible.
     */
    public float getMouseXHF() {
        return mouseXHF;
    }

    /**
     * The position of the mouse, relative to this gui element in as high of a resolution as possible.
     */
    public float getMouseYHF() {
        return mouseYHF;
    }

    /**
     * Check if the mouse is positioned within this context.
     */
    public boolean isHovered() {
        return mouseX >= 0 && mouseX < width && mouseY >= 0 && mouseY < height;
    }

    /**
     * Construct a new context that bleeds out over the boundaries of the existing context.
     * This is usually used for more fuzzy click detection.
     *
     * @param xBleed extra size in the negative and positive x direction
     * @param yBleed extra size in the negative and positive y direction
     */
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

    /**
     * Construct a new context representing that is located within this context.
     *
     * @param xOffset relative x position of the new context in the current context
     * @param yOffset relative y position of the new context in the current context
     * @param width   width of the new sub context
     * @param height  height of the new sub context
     */
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

    /**
     * Construct a new context representing that is located within this context. Does not translate the rendering offset.
     *
     * @param xOffset relative x position of the new context in the current context
     * @param yOffset relative y position of the new context in the current context
     * @param width   width of the new sub context
     * @param height  height of the new sub context
     */
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

    /**
     * Construct a new context, which has not been translated, but possible smaller if the arguments demand so.
     *
     * @param maxWidth max width of the new context. this argument will be ignored if it is larger than the current width
     * @param maxHeight max height of the new context. this argument will be ignored if it is larger than the current height
     */
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
