package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.common.KeyboardConstants;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;

public class ButtonComponent extends PanelComponent {
    private final Runnable onClick;

    public ButtonComponent(GuiComponent element, int insets, Runnable onClick, BackgroundRenderer panel) {
        super(element, insets, panel);
        this.onClick = onClick;
    }

    public ButtonComponent(GuiComponent element, int insets, Runnable onClick) {
        this(element, insets, onClick, DefaultBackgroundRenderer.DARK_RECT);
    }

    public Runnable getOnClick() {
        return onClick;
    }

    @Override
    public boolean mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        if (context.isHovered() && mouseEvent instanceof MouseEvent.Click) {
            MouseEvent.Click click = (MouseEvent.Click) mouseEvent;
            if (click.getMouseState() && click.getMouseButton() == 0) {
                onClick.run();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        if (isFocused() && event instanceof KeyboardEvent.KeyPressed) {
            KeyboardEvent.KeyPressed keyPressed = (KeyboardEvent.KeyPressed) event;
            if (keyPressed.getPressed() && keyPressed.getKeycode() == KeyboardConstants.INSTANCE.getEnter()) {
                onClick.run();
                return true;
            }
        }
        return false;
    }
}
