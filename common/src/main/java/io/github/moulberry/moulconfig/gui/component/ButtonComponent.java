package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.common.KeyboardConstants;
import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.gui.KeyboardEvent;
import io.github.moulberry.moulconfig.gui.MouseEvent;
import lombok.var;

public class ButtonComponent extends PanelComponent {
    final Runnable onClick;

    public ButtonComponent(GuiComponent element, int insets, Runnable onClick) {
        super(element, insets);
        this.onClick = onClick;
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent, GuiImmediateContext context) {
        if (context.isHovered() && mouseEvent instanceof MouseEvent.Click) {
            var click = ((MouseEvent.Click) mouseEvent);
            if (click.getMouseState() && click.getMouseButton() == 0)
                onClick.run();
        }
    }

    @Override
    public void keyboardEvent(KeyboardEvent event, GuiImmediateContext context) {
        if (isFocused() && event.getPress() && event.getKey() == KeyboardConstants.KEY_RETURN) {
            onClick.run();
        } else
            super.keyboardEvent(event, context);
    }
}
