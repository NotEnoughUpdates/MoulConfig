package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.observer.GetSetter

open class ItemStackComponent(
    val itemStack: GetSetter<IItemStack>,
) : GuiComponent() {
    override fun getWidth(): Int {
        return 18
    }

    override fun getHeight(): Int {
        return 18
    }

    override fun render(context: GuiImmediateContext) {
        context.renderContext.renderItemStack(itemStack.get(), 1, 1, "")
    }
}