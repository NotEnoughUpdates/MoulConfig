package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.GuiTextures
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import java.util.function.BiFunction
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
        context.renderContext.translate(-(width/3).toFloat(), 0f, 0f)
        context.renderContext.color(1f, 1f, 1f, 1f)
        mc.bindTexture(GuiTextures.SLIDER_ON_CAP)
        context.renderContext.drawTexturedRect(0F, 0F, 4F, context.height.toFloat())
        mc.bindTexture(GuiTextures.SLIDER_OFF_CAP)
        context.renderContext.drawTexturedRect((width - 4).toFloat(), 0F, 4F, context.height.toFloat())
        val sliderPosition = ((value.coerceIn(minValue..maxValue) - minValue) / (maxValue - minValue) * context.width).toInt()
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

        context.renderContext.translate(60f, -5f, 0f)
        componentNumberInput.render(context.translated(60, -5, componentNumberInput.width, 18))
    }

    fun setValueFromContext(context: GuiImmediateContext) {
        var v: Float = (context.mouseX + width/3) * (maxValue - minValue) / context.width + minValue
        v = min(v.toDouble(), maxValue.toDouble()).toFloat()
        v = max(v.toDouble(), minValue.toDouble()).toFloat()
        v = Math.round(v / minStep) * minStep
        value.set(v)
    }

    // I made this because I couldn't get isHovered to work with the translation
    private fun GuiImmediateContext.isHovered(): Boolean {
        return mouseX in 0 - width/3 until width && mouseY in 0 until height
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        if (!context.renderContext.isMouseButtonDown(0)) clicked = false
        if (context.isHovered() && mouseEvent is MouseEvent.Click && mouseEvent.mouseState && mouseEvent.mouseButton == 0) {
            clicked = true
        }
        if (clicked) {
            setValueFromContext(context)
        }
        return componentNumberInput.mouseEvent(mouseEvent, context.translated(45, -5, componentNumberInput.width, 18))
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        return componentNumberInput.keyboardEvent(event, context)
    }

    private val componentNumberInput by lazy {
        TextFieldComponent(
            object : GetSetter<String> {
                var editingBuffer: String = ""

                override fun get(): String {
                    if (isInFocus) return editingBuffer
                    var num: Float
                    try {
                        num = value.get()
                    } catch (e: NumberFormatException) {
                        num = 0f
                    }
                    val stringNum = num.toString().removeSuffix(".0")
                    return stringNum.also { editingBuffer = it }
                }

                override fun set(newValue: String) {
                    editingBuffer = newValue
                    if (isInFocus) return
                    var num: Float
                    try {
                        num = editingBuffer.toFloat()
                    } catch (e: NumberFormatException) {
                        num = 0f
                    }
                    value.set(num).also { editingBuffer = num.toString() }
                }
            },
            20,
            GetSetter.constant(true),
            "",
            IMinecraft.instance.defaultFontRenderer
        )
    }

    override fun <T : Any?> foldChildren(initial: T, visitor: BiFunction<GuiComponent, T, T>): T {
        return visitor.apply(componentNumberInput, initial)
    }
}
