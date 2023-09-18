package io.github.moulberry.moulconfig.gui.elements;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiElementButton extends GuiElementPanel {
    final Runnable onClick;

    public GuiElementButton(GuiElementNew element, int insets, Runnable onClick) {
        super(element, insets);
        this.onClick = onClick;
    }

    @Override
    public void mouseEvent(GuiImmediateContext context) {
        if (context.isHovered() && Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            onClick.run();
        }
    }

    @Override
    public void keyboardEvent(GuiImmediateContext context) {
        if (isFocused() && Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
            onClick.run();
        } else
            super.keyboardEvent(context);
    }
}
