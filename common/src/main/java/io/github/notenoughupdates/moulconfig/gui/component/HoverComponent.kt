package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import java.util.function.BiFunction
import java.util.function.Supplier

class HoverComponent(
    val child: GuiComponent,
    val hoverLines: Supplier<List<String>>,
) : GuiComponent() {
    override fun getWidth(): Int {
        return child.width
    }

    override fun getHeight(): Int {
        return child.height
    }

    override fun <T : Any?> foldChildren(
        initial: T,
        visitor: BiFunction<GuiComponent, T, T>
    ): T {
        return visitor.apply(child, initial)
    }

    override fun render(context: GuiImmediateContext) {
        if (context.isHovered) {
            context.renderContext.scheduleDrawTooltip(context.mouseX, context.mouseY, hoverLines.get())
        }
        child.render(context)

    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        return child.mouseEvent(mouseEvent, context)
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        return child.keyboardEvent(event, context)
    }
}
