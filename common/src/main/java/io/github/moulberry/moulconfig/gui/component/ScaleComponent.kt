package io.github.moulberry.moulconfig.gui.component

import io.github.moulberry.moulconfig.gui.GuiComponent
import io.github.moulberry.moulconfig.gui.GuiImmediateContext
import io.github.moulberry.moulconfig.gui.KeyboardEvent
import io.github.moulberry.moulconfig.gui.MouseEvent
import java.util.function.BiFunction
import java.util.function.Supplier

class ScaleComponent(
    val child: GuiComponent,
    val scaleFactor: Supplier<Float>,
) : GuiComponent() {
    override fun getWidth(): Int {
        return (scaleFactor.get() * child.width).toInt()
    }

    override fun getHeight(): Int {
        return (scaleFactor.get() * child.height).toInt()
    }

    override fun <T : Any?> foldChildren(initial: T, visitor: BiFunction<GuiComponent, T, T>): T {
        return visitor.apply(child, initial)
    }

    override fun render(context: GuiImmediateContext) {
        context.renderContext.pushMatrix()
        val s = scaleFactor.get()
        context.renderContext.scale(s, s, 1F)
        child.render(context.scaled(s))
        context.renderContext.popMatrix()
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext) {
        child.keyboardEvent(event, context.scaled(scaleFactor.get()))
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext) {
        super.mouseEvent(mouseEvent, context.scaled(scaleFactor.get()))
    }
}