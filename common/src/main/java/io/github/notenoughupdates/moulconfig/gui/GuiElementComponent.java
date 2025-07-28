package io.github.notenoughupdates.moulconfig.gui;

import io.github.notenoughupdates.moulconfig.internal.Warnings;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Adapts a {@link GuiElement} as a {@link GuiComponent} that renders on the entire screen. Not applicable to be used in transformed situations.
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class GuiElementComponent extends GuiComponent {
    GuiElement element;

    @Override
    public void setContext(GuiContext context) {
        super.setContext(context);
        if (context.getRoot() != this)
            Warnings.warn("Mounting GuiElementComponent at location other than root. This can cause issues.");
    }

    @Override
    public int getWidth() {
        return mc.getScaledWidth();
    }

    @Override
    public int getHeight() {
        return mc.getScaledHeight();
    }

    @Override
    public void render(@NotNull GuiImmediateContext context) {
        if (context.getRenderOffsetX() != 0 || context.getRenderOffsetY() != 0) {
            Warnings.warn("Cannot render GuiElement with a pretransformed matrix stack");
        }
        element.render();
    }

    @Override
    public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
        return element.mouseInput(context.getMouseX(), context.getMouseY(), mouseEvent);
    }

    @Override
    public boolean keyboardEvent(@NotNull KeyboardEvent event, @NotNull GuiImmediateContext context) {
        return element.keyboardInput(event);
    }
}
