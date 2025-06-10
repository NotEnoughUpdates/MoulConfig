package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.common.KeyboardConstants
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.gui.MouseEvent.Click
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import java.util.function.Supplier
import kotlin.math.max
import kotlin.math.min

open class TextFieldComponent(
    val text: GetSetter<String>,
    private val preferredWidth: Int,
    val editable: Supplier<Boolean> = GetSetter.constant(true),
    val suggestion: String = "",
    val font: IFontRenderer = IMinecraft.instance.defaultFontRenderer,
) : GuiComponent() {
    private var cursor = 0
    private var selection = -1
    private var scrollOffset = 0
    private var visibleText: String? = null
    override fun getWidth(): Int {
        if (isFocused) return max(preferredWidth, font.getStringWidth(text.get()) + 10)
        return preferredWidth
    }

    override fun getHeight(): Int {
        return 14
    }

    open fun scrollCursorIntoView(width: Int) {
        validateCursor()
        if (scrollOffset > cursor) scrollOffset = cursor
        if (scrollOffset < cursor &&
            font.trimStringToWidth(
                safeSubString(text.get(), scrollOffset),
                width - TEXT_PADDING_X * 2
            ).length + scrollOffset < cursor
        ) {
            scrollOffset = cursor
        }
        checkScrollOffset(width)
    }

    open fun checkScrollOffset(width: Int) {
        val text = text.get()
        val rightMostScrollOffset = text.length - font.trimStringToWidth(text, width - TEXT_PADDING_X * 2, true).length
        scrollOffset = max(0, min(rightMostScrollOffset, scrollOffset))
    }

    fun updateVisibleText(width: Int) {
        visibleText =
            font.trimStringToWidth(safeSubString(text.get(), scrollOffset), width - TEXT_PADDING_X * 2)

    }

    override fun render(context: GuiImmediateContext) {
        validateCursor()
        checkScrollOffset(context.width)
        updateVisibleText(context.width)
        renderBox(context)
        renderText(context, visibleText!!)
        if (text.get().isEmpty() && !isFocused) {
            context.renderContext.drawString(
                font,
                suggestion,
                TEXT_PADDING_X,
                context.height / 2 - font.height / 2,
                SUGGESTION_COLOR,
                false
            )
        }
        if (isFocused) {
            renderCursor(context)
        }
        renderSelection(context)
    }

    open fun validateCursor() {
        cursor = max(0, min(text.get().length, cursor))
    }

    private fun renderSelection(context: GuiImmediateContext) {
        if (selection == cursor || selection == -1) return
        val left = min(cursor, selection)
        val right = max(cursor, selection)
        if (right < scrollOffset || left > scrollOffset + visibleText!!.length) return
        val normalizedLeft = max(scrollOffset, left) - scrollOffset
        val normalizedRight =
            min(scrollOffset + visibleText!!.length, right) - scrollOffset
        val leftPos = font.getStringWidth(safeSubString(visibleText!!, 0, normalizedLeft))
        val rightPos = leftPos + font.getStringWidth(safeSubString(visibleText!!, normalizedLeft, normalizedRight))
        context.renderContext.invertedRect(
            (TEXT_PADDING_X + leftPos).toFloat(),
            TEXT_PADDING_Y.toFloat(),
            (TEXT_PADDING_X + rightPos).toFloat(),
            (context.height - TEXT_PADDING_Y).toFloat()
        )
    }

    private fun renderCursor(context: GuiImmediateContext) {
        if (System.currentTimeMillis() / 1000 % 2 == 0L) {
            return
        }
        if (cursor < scrollOffset) return
        if (cursor > scrollOffset + visibleText!!.length) return
        val cursorOffset = font.getStringWidth(safeSubString(visibleText!!, 0, cursor - scrollOffset))
        context.renderContext.drawColoredRect(
            (TEXT_PADDING_X + cursorOffset).toFloat(),
            TEXT_PADDING_Y.toFloat(),
            (TEXT_PADDING_X + cursorOffset + 1).toFloat(),
            (context.height - TEXT_PADDING_Y).toFloat(),
            CURSOR_COLOR
        )
    }

    private fun renderText(context: GuiImmediateContext, visibleText: String) {
        val textColor = if (editable.get()) ENABLED_COLOR else DISABLED_COLOR
        context.renderContext.drawString(
            font, visibleText, TEXT_PADDING_X,
            context.height / 2 - font.height / 2, textColor, true
        )
    }

    private fun renderBox(context: GuiImmediateContext) {
        val borderColor = if (isFocused) BORDER_COLOR_SELECTED else BORDER_COLOR_UNSELECTED
        context.renderContext.drawColoredRect(0f, 0f, context.width.toFloat(), context.height.toFloat(), borderColor)
        context.renderContext.drawColoredRect(
            1f,
            1f,
            (context.width - 1).toFloat(),
            (context.height - 1).toFloat(),
            BACKGROUND_COLOR
        )
    }

    override fun keyboardEvent(event: KeyboardEvent, context: GuiImmediateContext): Boolean {
        if (!editable.get()) return false
        if (!isFocused) return false
        if (event is KeyboardEvent.KeyPressed && event.pressed) {
            return when (event.keycode) {
                KeyboardConstants.left -> {
                    onDirectionalKey(context, -1)
                    return true
                }

                KeyboardConstants.right -> {
                    onDirectionalKey(context, 1)
                    return true
                }

                KeyboardConstants.home, KeyboardConstants.up -> {
                    if (context.renderContext.isShiftDown) {
                        if (selection == -1) selection = cursor
                    } else {
                        selection = -1
                    }
                    cursor = 0
                    scrollCursorIntoView(context.width)
                    return true
                }

                KeyboardConstants.down, KeyboardConstants.end -> {
                    if (context.renderContext.isShiftDown) {
                        if (selection == -1) selection = cursor
                    } else {
                        selection = -1
                    }
                    cursor = text.get().length
                    scrollCursorIntoView(context.width)
                    return true
                }

                KeyboardConstants.backSpace -> {
                    if (selection == -1) selection = skipCharacters(context.renderContext.isCtrlDown, -1)
                    writeText("", context.width)
                    return true
                }

                KeyboardConstants.delete -> {
                    if (selection == -1) selection = skipCharacters(context.renderContext.isCtrlDown, 1)
                    writeText("", context.width)
                    return true
                }

                KeyboardConstants.keyC -> if (context.renderContext.isCtrlDown) {
                    IMinecraft.instance.copyToClipboard(
                        getSelection()
                    )
                    return true
                } else {
                    return false
                }

                KeyboardConstants.keyX -> if (context.renderContext.isCtrlDown) {
                    IMinecraft.instance.copyToClipboard(
                        getSelection()
                    )
                    writeText("", context.width)
                    return true
                } else {
                    return false
                }

                KeyboardConstants.keyV -> if (context.renderContext.isCtrlDown) {
                    writeText(IMinecraft.instance.copyFromClipboard(), context.width)
                    return true
                } else {
                    return false
                }

                KeyboardConstants.keyA -> if (context.renderContext.isCtrlDown) {
                    cursor = text.get().length
                    selection = 0
                    scrollCursorIntoView(context.width)
                    return true
                } else {
                    return false
                }

                else -> return false
            }
        } else if (event is KeyboardEvent.CharTyped) {
            val it = event.char
            var anyWritten = false
            if (it >= ' ' && it != '§' && it.code != 127) {
                writeText(it + "", context.width)
                anyWritten = true
            }
            return anyWritten
        } else {
            return false
        }
    }

    private fun getSelection(): String {
        if (selection == -1) return ""
        val l = min(cursor, selection)
        val r = max(cursor, selection)
        return safeSubString(text.get(), l, r)
    }

    override fun mouseEvent(mouseEvent: MouseEvent, context: GuiImmediateContext): Boolean {
        super.mouseEvent(mouseEvent, context)
        checkScrollOffset(context.width)
        updateVisibleText(context.width)
        if (mouseEvent is Click && mouseEvent.mouseState) {
            if (context.isHovered) {
                requestFocus()
                return true
            } else {
                setFocus(false)
            }
        }
        return false
    }

    private fun safeSubString(str: String, startIndex: Int): String {
        return str.substring(min(startIndex, str.length))
    }

    private fun safeSubString(str: String, startIndex: Int, endIndex: Int): String {
        return str.substring(
            min(startIndex, str.length),
            min(max(startIndex, endIndex), str.length)
        )
    }

    fun writeText(s: String, width: Int) {
        val t = text.get()
        if (selection == -1) {
            text.set(safeSubString(t, 0, cursor) + s + safeSubString(t, cursor))
            cursor += s.length
        } else {
            val l = min(cursor, selection)
            val r = max(cursor, selection)
            text.set(safeSubString(t, 0, l) + s + safeSubString(t, r))
            cursor = l + s.length
            selection = -1
        }
        scrollCursorIntoView(width)
    }

    open fun onDirectionalKey(context: GuiImmediateContext, i: Int) {
        if (context.renderContext.isShiftDown) {
            if (selection == -1) selection = cursor
            cursor = skipCharacters(context.renderContext.isCtrlDown, i)
        } else {
            if (selection != -1) {
                cursor = if (i < 0)
                    min(cursor, selection)
                else
                    max(cursor, selection)
                selection = -1
            } else {
                cursor = skipCharacters(context.renderContext.isCtrlDown, i)
            }
        }
        scrollCursorIntoView(context.width)
    }

    private fun skipCharacters(skipWords: Boolean, i: Int): Int {
        if (i != -1 && i != 1) return cursor
        var position = cursor
        while (true) {
            position += i
            if (position < 0) return 0
            if (position > text.get().length) return text.get().length
            if (!skipWords) return position
            if (position < text.get().length && Character.isWhitespace(text.get()[position])) return position
        }
    }

    companion object {
        private const val TEXT_PADDING_X = 4
        private const val BACKGROUND_COLOR = -0x1000000
        private const val BORDER_COLOR_SELECTED = 0xFF00FF00.toInt()
        private const val BORDER_COLOR_UNSELECTED = 0xFFFFFFFF.toInt()
        private const val ENABLED_COLOR = -0x1f1f20
        private const val SUGGESTION_COLOR = -0x7f7f80
        private const val DISABLED_COLOR = -0x8f8f90
        private const val CURSOR_COLOR = -0x2f2f30
        private const val TEXT_PADDING_Y = 2
    }
}
