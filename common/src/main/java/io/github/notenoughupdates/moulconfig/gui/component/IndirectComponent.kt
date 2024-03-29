package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import java.util.function.BiFunction
import java.util.function.Supplier

open class IndirectComponent(
    val component: Supplier<out GuiComponent>
) : GuiComponent() {
    override fun getWidth(): Int {
        return component.get().width
    }

    override fun getHeight(): Int {
        return component.get().height
    }

    override fun render(context: GuiImmediateContext) {
        component.get().render(context)
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        return component.get().keyboardEvent(event, context)
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        return component.get().mouseEvent(mouseEvent, context)
    }

    override fun <T : Any?> foldChildren(
        initial: T,
        visitor: BiFunction<GuiComponent, T, T>
    ): T {
        return visitor.apply(component.get(), initial)
    }
}