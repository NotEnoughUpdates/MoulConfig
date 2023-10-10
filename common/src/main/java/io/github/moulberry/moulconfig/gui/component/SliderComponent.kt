package io.github.moulberry.moulconfig.gui.component

import io.github.moulberry.moulconfig.GuiTextures
import io.github.moulberry.moulconfig.gui.GuiComponent
import io.github.moulberry.moulconfig.gui.GuiImmediateContext
import io.github.moulberry.moulconfig.gui.MouseEvent
import io.github.moulberry.moulconfig.observer.GetSetter
import kotlin.math.max
import kotlin.math.min

open class SliderComponent(
    val value: GetSetter<Float>,
    val minValue: Float,
    val maxValue: Float,
    val minStep: Float,
    private val width: Int,
) : GuiComponent() {
    var clicked: Boolean = false
    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return 16
    }

    override fun render(context: GuiImmediateContext) {
        if (clicked) {
            setValueFromContext(context)
        }
        val value: Float = value.get()
        context.renderContext.color(1f, 1f, 1f, 1f)
        mc.bindTexture(GuiTextures.SLIDER_ON_CAP)
        context.renderContext.drawTexturedRect(0F, 0F, 4F, context.height.toFloat())
        mc.bindTexture(GuiTextures.SLIDER_OFF_CAP)
        context.renderContext.drawTexturedRect((width - 4).toFloat(), 0F, 4F, context.height.toFloat())
        val sliderPosition = ((value - minValue) / (maxValue - minValue) * context.width).toInt()
        if (sliderPosition > 5) {
            mc.bindTexture(GuiTextures.SLIDER_ON_SEGMENT)
            context.renderContext.drawTexturedRect(4F, 0F, (sliderPosition - 4).toFloat(), context.height.toFloat())
        }
        if (sliderPosition < context.width - 5) {
            mc.bindTexture(GuiTextures.SLIDER_OFF_SEGMENT)
            context.renderContext.drawTexturedRect(
                sliderPosition.toFloat(),
                0F,
                (context.width - 4 - sliderPosition).toFloat(),
                context.height.toFloat()
            )
        }
        for (i in 0..3) {
            val notchX = context.width * i / 4 - 1
            mc.bindTexture(if (notchX > sliderPosition) GuiTextures.SLIDER_OFF_NOTCH else GuiTextures.SLIDER_ON_NOTCH)
            context.renderContext.drawTexturedRect(notchX.toFloat(), (context.height - 4) / 2F, 2F, 4F)
        }
        mc.bindTexture(GuiTextures.SLIDER_BUTTON)
        context.renderContext.drawTexturedRect((sliderPosition - 4).toFloat(), 0F, 8F, context.height.toFloat())
    }

    fun setValueFromContext(context: GuiImmediateContext) {
        var v: Float = context.mouseX * (maxValue - minValue) / context.width + minValue
        v = min(v.toDouble(), maxValue.toDouble()).toFloat()
        v = max(v.toDouble(), minValue.toDouble()).toFloat()
        v = Math.round(v / minStep) * minStep
        value.set(v)
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext) {
        if (!context.renderContext.isMouseButtonDown(0)) clicked = false
        if (context.isHovered && mouseEvent is MouseEvent.Click && mouseEvent.mouseState && mouseEvent.mouseButton == 0) {
            clicked = true
        }
        if (clicked) {
            setValueFromContext(context)
        }
    }
}
