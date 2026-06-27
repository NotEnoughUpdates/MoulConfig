package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import java.util.function.BiFunction
import kotlin.math.max

open class SliderWithTextComponent(
    value: GetSetter<Float>,
    minValue: Float,
    maxValue: Float,
    minStep: Float,
    width: Int,
) : SliderComponent(value, minValue, maxValue, minStep, width) {
    private val sliderWidth = width

    override fun getWidth(): Int = SLIDER_INSET + sliderWidth + INPUT_GAP + componentNumberInput.width

    override fun getHeight(): Int = max(super.getHeight(), INPUT_HEIGHT)

    override fun render(context: GuiImmediateContext) {
        renderSlider(context)
        renderInput(context)
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        val sliderHandled = super.mouseEvent(mouseEvent, sliderContext(context))
        val inputHandled = componentNumberInput.mouseEvent(mouseEvent, inputContext(context))
        return sliderHandled || inputHandled
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        componentNumberInput.setShouldExpandToFit(true)
        return componentNumberInput.keyboardEvent(event, inputContext(context))
    }

    private fun renderSlider(context: GuiImmediateContext) {
        context.renderContext.pushMatrix()
        context.renderContext.translate(SLIDER_INSET.toFloat(), sliderY().toFloat())
        super.render(sliderContext(context))
        context.renderContext.popMatrix()
    }

    private fun renderInput(context: GuiImmediateContext) {
        context.renderContext.pushMatrix()
        context.renderContext.translate(inputX(context).toFloat(), inputY().toFloat())
        componentNumberInput.render(inputContext(context))
        context.renderContext.popMatrix()
    }

    private fun sliderContext(context: GuiImmediateContext): GuiImmediateContext =
        context.translated(SLIDER_INSET, sliderY(), effectiveSliderWidth(context), super.getHeight())

    private fun inputContext(context: GuiImmediateContext): GuiImmediateContext =
        context.translated(inputX(context), inputY(), componentNumberInput.width, INPUT_HEIGHT)

    private fun effectiveSliderWidth(context: GuiImmediateContext): Int =
        (context.width - SLIDER_INSET - INPUT_GAP - componentNumberInput.width).coerceIn(MIN_SLIDER_WIDTH, sliderWidth)

    private fun sliderY(): Int = (getHeight() - super.getHeight()) / 2

    private fun inputX(context: GuiImmediateContext): Int = SLIDER_INSET + effectiveSliderWidth(context) + INPUT_GAP

    private fun inputY(): Int = (getHeight() - INPUT_HEIGHT) / 2

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
            IMinecraft.INSTANCE.defaultFontRenderer
        )
    }

    override fun <T : Any?> foldChildren(initial: T, visitor: BiFunction<GuiComponent, T, T>): T {
        return visitor.apply(componentNumberInput, initial)
    }

    private companion object {
        private const val SLIDER_INSET = 10
        private const val INPUT_GAP = 10
        private const val INPUT_HEIGHT = 18
        private const val MIN_SLIDER_WIDTH = 35
    }
}
