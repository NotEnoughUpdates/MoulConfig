package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.HorizontalAlign
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.gui.VerticalAlign
import java.util.function.BiFunction
import java.util.function.Supplier

data class AlignComponent(
    val child: GuiComponent,
    val horizontal: Supplier<HorizontalAlign>,
    val vertical: Supplier<VerticalAlign>,
) : GuiComponent() {
    override fun getWidth(): Int {
        return child.width
    }

    override fun getHeight(): Int {
        return child.height
    }

    fun getChildContext(context: GuiImmediateContext): GuiImmediateContext {
        return context.translated(
            getChildOffsetX(context),
            getChildOffsetY(context),
            child.width,
            child.height
        )
    }

    fun getChildOffsetX(context: GuiImmediateContext): Int {
        return when (horizontal.get()) {
            HorizontalAlign.LEFT -> 0
            HorizontalAlign.CENTER -> context.width / 2 - child.width / 2
            HorizontalAlign.RIGHT -> context.width - child.width
        }
    }

    fun getChildOffsetY(context: GuiImmediateContext): Int {
        return when (vertical.get()) {
            VerticalAlign.BOTTOM -> context.height - child.height
            VerticalAlign.CENTER -> context.height / 2 - child.height / 2
            VerticalAlign.TOP -> 0
        }
    }

    override fun <T> foldChildren(initial: T, visitor: BiFunction<GuiComponent, T, T>): T {
        return visitor.apply(child, initial)
    }

    override fun render(context: GuiImmediateContext) {
        context.renderContext.pushMatrix()
        context.renderContext.translate(getChildOffsetX(context).toFloat(), getChildOffsetY(context).toFloat())
        child.render(getChildContext(context))
        context.renderContext.popMatrix()
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        return child.keyboardEvent(event, getChildContext(context))
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        return child.mouseEvent(mouseEvent, getChildContext(context))
    }
}
