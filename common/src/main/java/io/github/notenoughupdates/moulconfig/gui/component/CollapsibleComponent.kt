package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import java.util.function.Supplier

class CollapsibleComponent(
    val title: Supplier<GuiComponent>,
    val body: Supplier<GuiComponent>,
    val collapsedState: GetSetter<Boolean> = GetSetter.floating(
        true
    )
) : GuiComponent() {

    companion object {
        val fr = IMinecraft.instance.defaultFontRenderer
        val padding = 2
        val trim = 3
        val rightTriangle = '▶'.toString()
        val rightSize = fr.getStringWidth(rightTriangle)
        val bottomTriangle = '▼'.toString()
        val bottomSize = fr.getStringWidth(bottomTriangle)
        val bottomOffset = (rightSize - bottomSize).coerceAtLeast(0) / 2
        val rightOffset = (bottomSize - rightSize).coerceAtLeast(0) / 2
        val iconWidth = maxOf(rightSize, bottomSize)
    }

    override fun getWidth(): Int {
        return maxOf(title.get().width + padding + iconWidth, body.get().width)
    }

    override fun getHeight(): Int {
        return if (collapsedState.get()) {
            maxOf(title.get().height, fr.height)
        } else {
            maxOf(title.get().height, fr.height) + trim + body.get().height
        }
    }

    override fun render(context: GuiImmediateContext) {
        val collapsed = collapsedState.get()
        context.renderContext.drawString(
            fr,
            if (collapsed) rightTriangle else bottomTriangle,
            if (collapsed) rightOffset else bottomOffset, 0, -1,
            false
        )

        val barHeight = maxOf(title.get().height, fr.height)
        context.renderContext.pushMatrix()
        context.renderContext.translate(iconWidth.toFloat(), 0F)
        title.get().render(context.translated(iconWidth, 0, context.width - iconWidth, barHeight))
        context.renderContext.popMatrix()

        if (!collapsed) {
            context.renderContext.drawColoredRect(
                0F,
                barHeight + 1F,
                context.width.toFloat(),
                barHeight + 2F,
                0xFF000000.toInt()
            )

            context.renderContext.pushMatrix()
            context.renderContext.translate(0F, barHeight.toFloat())
            body.get().render(
                context.translated(0, barHeight, context.width, context.height - barHeight)
            )
            context.renderContext.popMatrix()
        }
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        val barHeight = maxOf(title.get().height, fr.height)

        if (mouseEvent is MouseEvent.Click && context.translated(
                0,
                0,
                context.width,
                barHeight
            ).isHovered
        ) {
            if (mouseEvent.mouseState)
                collapsedState.set(!collapsedState.get())
            return true
        }

        return title.get().mouseEvent(
            mouseEvent, context.translated(
                iconWidth,
                0,
                context.width - iconWidth,
                barHeight
            )
        ) || body.get().mouseEvent(
            mouseEvent, context.translated(0, barHeight, context.width, context.height - barHeight)
        )
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        val barHeight = maxOf(title.get().height, fr.height)
        return title.get().keyboardEvent(
            event, context.translated(
                iconWidth,
                0,
                context.width - iconWidth,
                barHeight
            )
        ) || body.get().keyboardEvent(
            event, context.translated(0, barHeight, context.width, context.height - barHeight)
        )
    }
}
