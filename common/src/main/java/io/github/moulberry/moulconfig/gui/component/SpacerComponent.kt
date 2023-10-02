package io.github.moulberry.moulconfig.gui.component

import io.github.moulberry.moulconfig.gui.GuiComponent
import io.github.moulberry.moulconfig.gui.GuiImmediateContext
import java.util.function.Supplier

class SpacerComponent(
    val width: Supplier<Int>,
    val height: Supplier<Int>,
) : GuiComponent() {
    override fun getWidth(): Int {
        return width.get()
    }

    override fun getHeight(): Int {
        return height.get()
    }

    override fun render(context: GuiImmediateContext) {
    }
}