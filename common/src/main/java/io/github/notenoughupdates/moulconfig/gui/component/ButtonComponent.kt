package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.common.KeyboardConstants
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent.Click

class ButtonComponent(
    element: GuiComponent,
    insets: Int,
    val onClick: Runnable
) : PanelComponent(element, insets, DefaultBackgroundRenderer.DARK_RECT) {
    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        if (context.isHovered && mouseEvent is Click) {
            val (mouseButton, mouseState) = mouseEvent
            if (mouseState && mouseButton == 0) {
                onClick.run()
                return true
            }
        }
        return false
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        if (isFocused && event is KeyboardEvent.KeyPressed &&
            event.pressed && event.keycode == KeyboardConstants.enter
        ) {
            onClick.run()
            return true
        }
        return false
    }
}
