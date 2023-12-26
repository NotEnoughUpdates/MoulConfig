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
package io.github.moulberry.moulconfig.gui

import io.github.moulberry.moulconfig.common.RenderContext

/**
 * A context containing the constraints of a gui elements, as well as the state of the user interface, relative to that gui element.
 */
data class GuiImmediateContext(
    val renderContext: RenderContext,

    /**
     * The current absolute offset for this gui context. This should not need to be accessed, unless you are contacting some API that does not access GlStateManager.
     */
    val renderOffsetX: Int,

    /**
     * The current absolute offset for this gui context. This should not need to be accessed, unless you are contacting some API that does not access GlStateManager.
     */
    val renderOffsetY: Int,

    /**
     * The available width for that gui element to render in.
     */
    val width: Int,

    /**
     * The available height for that gui element to render in.
     */
    val height: Int,

    /**
     * The position of the mouse, relative to this gui element.
     */
    val mouseX: Int,

    /**
     * The position of the mouse, relative to this gui element.
     */
    val mouseY: Int,

    /**
     * The position of the mouse, relative to the root element.
     */
    val absoluteMouseX: Int,

    /**
     * The position of the mouse, relative to the root element.
     */
    val absoluteMouseY: Int,
) {
    val isHovered: Boolean
        /**
         * Check if the mouse is positioned within this context.
         */
        get() = mouseX in 0 until width && mouseY in 0 until height

    /**
     * Construct a new context that bleeds out over the boundaries of the existing context.
     * This is usually used for more fuzzy click detection.
     *
     * @param xBleed extra size in the negative and positive x direction
     * @param yBleed extra size in the negative and positive y direction
     */
    fun withBleed(xBleed: Int, yBleed: Int): GuiImmediateContext {
        return GuiImmediateContext(
            renderContext,
            renderOffsetX - xBleed, renderOffsetY - yBleed, width + 2 * xBleed, height + 2 * yBleed,
            mouseX + xBleed, mouseY + yBleed,
            absoluteMouseX, absoluteMouseY
        )
    }

    /**
     * Construct a new context representing that is located within this context.
     *
     * @param xOffset relative x position of the new context in the current context
     * @param yOffset relative y position of the new context in the current context
     * @param width   width of the new sub context
     * @param height  height of the new sub context
     */
    fun translated(xOffset: Int, yOffset: Int, width: Int, height: Int): GuiImmediateContext {
        return GuiImmediateContext(
            renderContext,
            renderOffsetX + xOffset,
            renderOffsetY + yOffset,
            width,
            height,
            mouseX - xOffset,
            mouseY - yOffset,
            absoluteMouseX,
            absoluteMouseY
        )
    }

    /**
     * Construct a new context representing that is located within this context. Does not translate the rendering offset.
     *
     * @param xOffset relative x position of the new context in the current context
     * @param yOffset relative y position of the new context in the current context
     * @param width   width of the new sub context
     * @param height  height of the new sub context
     */
    fun translatedNonRendering(xOffset: Int, yOffset: Int, width: Int, height: Int): GuiImmediateContext {
        return GuiImmediateContext(
            renderContext, renderOffsetX, renderOffsetY, width, height, mouseX - xOffset, mouseY - yOffset,
            absoluteMouseX, absoluteMouseY
        )
    }

    /**
     * Construct a new context, which has not been translated, but possible smaller if the arguments demand so.
     *
     * @param maxWidth max width of the new context. this argument will be ignored if it is larger than the current width
     * @param maxHeight max height of the new context. this argument will be ignored if it is larger than the current height
     */
    fun limitSize(maxWidth: Int, maxHeight: Int): GuiImmediateContext {
        return translated(0, 0, minOf(width, maxWidth), minOf(height, maxHeight))
    }

    fun scaled(scale: Float): GuiImmediateContext {
        return GuiImmediateContext(
            renderContext,
            renderOffsetX, renderOffsetY,
            (width / scale).toInt(), (height / scale).toInt(),
            ((mouseX - renderOffsetX) * scale).toInt(), ((mouseY - renderOffsetY) * scale).toInt(),
            absoluteMouseX, absoluteMouseY
        )
    }
}
