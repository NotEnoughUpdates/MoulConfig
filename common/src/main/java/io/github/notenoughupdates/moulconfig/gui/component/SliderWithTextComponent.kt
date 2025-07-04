package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import java.util.function.BiFunction
import kotlin.math.max
import kotlin.math.min

open class SliderWithTextComponent(
    value: GetSetter<Float>,
    minValue: Float,
    maxValue: Float,
    minStep: Float,
    width: Int,
) : SliderComponent(value, minValue, maxValue, minStep, width) {


    override fun render(context: GuiImmediateContext) {
        context.renderContext.translate(-(width/3).toFloat(), 0f)
        super.render(context)
        context.renderContext.translate(60f, -5f)
        componentNumberInput.width
        componentNumberInput.render(context.translated(60, -5, componentNumberInput.width, 18))
    }

    // I made this because I couldn't get isHovered to work with the translation
    private fun GuiImmediateContext.isHovered(): Boolean {
        return mouseX in 0 - width/3 until width - 13 && mouseY in 0 until height
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        if (!context.renderContext.isMouseButtonDown(0)) clicked = false
        if (context.isHovered() && mouseEvent is MouseEvent.Click && mouseEvent.mouseState && mouseEvent.mouseButton == 0) {
            clicked = true
        }
        if (clicked) {
            setValueFromContext(context)
            return true
        }
        return componentNumberInput.mouseEvent(mouseEvent, context.translated(45, -5, componentNumberInput.width, 18))
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        componentNumberInput.setShouldExpandToFit(true)
        return componentNumberInput.keyboardEvent(event, context)
    }

    override fun setValueFromContext(context: GuiImmediateContext) {
        var v: Float = (context.mouseX + width/3) * (maxValue - minValue) / context.width + minValue
        v = min(v.toDouble(), maxValue.toDouble()).toFloat()
        v = max(v.toDouble(), minValue.toDouble()).toFloat()
        v = Math.round(v / minStep) * minStep
        value.set(v)
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
