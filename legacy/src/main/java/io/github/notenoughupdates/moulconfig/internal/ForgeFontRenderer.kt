package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiUtilRenderComponents
import java.util.regex.Pattern


class ForgeFontRenderer(val font: FontRenderer) : IFontRenderer {
    override val height: Int
        get() = font.FONT_HEIGHT

    override fun getStringWidth(string: StructuredText): Int {
        return font.getStringWidth(StructuredTextImpl.unwrap(string).formattedText)
    }

    override fun getStringWidth(string: String): Int {
        return font.getStringWidth(string)
    }

    override fun getCharWidth(char: Char): Int {
        return font.getCharWidth(char)
    }

    override fun splitText(text: StructuredText, width: Int): List<StructuredText> {
        val iChatComponents =
            GuiUtilRenderComponents.splitText(StructuredTextImpl.unwrap(text), width, font, false, false)
        return iChatComponents.map { StructuredTextImpl.wrap(it) }
    }

    override fun splitLines(text: StructuredText): List<StructuredText> {
        return splitText(text, Integer.MAX_VALUE)
    }

    override fun trimStringToWidth(string: String, width: Int, reverse: Boolean): String {
        return font.trimStringToWidth(string, width, reverse)
    }
}
