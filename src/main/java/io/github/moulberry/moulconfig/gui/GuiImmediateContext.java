package io.github.moulberry.moulconfig.gui;

import lombok.Value;

/**
 * A context containing the constraints of a gui elements, as well as the state of the user interface, relative to that gui element.
 */
@Value
public class GuiImmediateContext {
    /**
     * The available width for that gui element to render in.
     */
    int width;
    /**
     * The available height for that gui element to render in.
     */
    int height;
    /**
     * The position of the mouse, relative to this gui element.
     */
    int mouseX;
    /**
     * The position of the mouse, relative to this gui element.
     */
    int mouseY;
    /**
     * The position of the mouse, relative to the root element.
     */
    int absoluteMouseX;
    /**
     * The position of the mouse, relative to the root element.
     */
    int absoluteMouseY;

    /**
     * Check if the mouse is positioned within this context.
     */
    public boolean isHovered() {
        return 0 <= mouseX && mouseX < width
            && 0 <= mouseY && mouseY < height;
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
            width + 2 * xBleed, height + 2 * yBleed,
            mouseX + xBleed, mouseY + yBleed,
            absoluteMouseX, absoluteMouseY
        );
    }

    /**
     * Construct a new context representing that is located within this context.
     *
     * @param xOffset relative x position of the new context in the currrent context
     * @param yOffset relative y position of the new context in the currrent context
     * @param width   width of the new sub context
     * @param height  height of the new sub context
     */
    public GuiImmediateContext translated(int xOffset, int yOffset, int width, int height) {
        return new GuiImmediateContext(
            width, height, mouseX - xOffset, mouseY - yOffset,
            absoluteMouseX, absoluteMouseY
        );
    }

}
