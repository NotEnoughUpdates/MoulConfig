package io.github.notenoughupdates.moulconfig.gui

import io.github.notenoughupdates.moulconfig.gui.component.TextFieldComponent
import io.github.notenoughupdates.moulconfig.observer.GetSetter

class ClassResizableTextField(text: GetSetter<String>) : TextFieldComponent(
    text,
    20,
) {
    private var width = 20

    fun setWidth(width: Int) {
        this.width = width
    }

    override fun getWidth(): Int {
        return width
    }

    override fun render(context: GuiImmediateContext) {
        super.render(context.translated(0, 0, width, 18))
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        return super.mouseEvent(mouseEvent, context.translated(0, 0, width, 18))
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        return super.keyboardEvent(event, context.translated(0, 0, width, 18))
    }

}