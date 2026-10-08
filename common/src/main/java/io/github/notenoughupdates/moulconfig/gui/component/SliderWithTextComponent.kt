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

    override fun render(context: GuiImmediateContext) {
        super.render(sliderContext(context))
        // translated() does not touch the render matrix, so the number box is positioned by hand
        context.renderContext.pushMatrix()
        context.renderContext.translate(numberX(context).toFloat(), numberY(context).toFloat())
        numberInput.render(numberContext(context))
        context.renderContext.popMatrix()
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        if (!context.renderContext.isMouseButtonDown(IMinecraft.INSTANCE.mouseConstants.left())) clicked = false
        if (numberInput.mouseEvent(mouseEvent, numberContext(context))) return true
        return super.mouseEvent(mouseEvent, sliderContext(context))
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        numberInput.setShouldExpandToFit(true)
        return numberInput.keyboardEvent(event, numberContext(context))
    }

    private fun trackWidth(context: GuiImmediateContext): Int {
        return max(minWidth, context.width - numberWidth(context) - NUMBER_GAP)
    }

    private fun numberWidth(context: GuiImmediateContext): Int {
        val slack = context.width - minWidth - NUMBER_GAP
        return NUMBER_WIDTH.coerceIn(MIN_NUMBER_WIDTH, max(MIN_NUMBER_WIDTH, slack))
    }

    private fun numberX(context: GuiImmediateContext): Int {
        return trackWidth(context) + NUMBER_GAP
    }

    private fun numberY(context: GuiImmediateContext): Int {
        return (context.height - numberInput.getHeight()) / 2
    }

    private fun sliderContext(context: GuiImmediateContext): GuiImmediateContext {
        return context.translated(0, 0, trackWidth(context), context.height)
    }

    private fun numberContext(context: GuiImmediateContext): GuiImmediateContext {
        return context.translated(
            numberX(context),
            numberY(context),
            numberWidth(context),
            numberInput.getHeight()
        )
    }

    private val numberInput by lazy {
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
            NUMBER_WIDTH,
            GetSetter.constant(true),
            "",
            IMinecraft.INSTANCE.defaultFontRenderer
        )
    }

    override fun <T : Any?> foldChildren(initial: T, visitor: BiFunction<GuiComponent, T, T>): T {
        return visitor.apply(numberInput, initial)
    }

    companion object {
        private const val NUMBER_WIDTH = 30
        private const val MIN_NUMBER_WIDTH = 20
        private const val NUMBER_GAP = 4
    }
}
