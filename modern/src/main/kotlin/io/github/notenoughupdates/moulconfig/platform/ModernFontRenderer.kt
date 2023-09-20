package io.github.notenoughupdates.moulconfig.platform

import io.github.moulberry.moulconfig.common.IFontRenderer
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.Text

class ModernFontRenderer(val textRenderer: TextRenderer) :
    IFontRenderer {
    override val height: Int
        get() = textRenderer.fontHeight

    override fun getStringWidth(string: String): Int {
        return textRenderer.getWidth(string)
    }

    override fun getCharWidth(char: Char): Int {
        return textRenderer.getWidth(char + "")
    }

    override fun splitText(text: String, width: Int): List<String> {
        val lines = ChatMessages.breakRenderedChatMessageLines(Text.literal(text), width, textRenderer)
        val strings: MutableList<String> = ArrayList(lines.size)
        for (iChatComponent in lines) {
            var formattedText = ""
            iChatComponent.accept { i, style, j ->
                formattedText += j.toChar()
                true
            }
            strings.add(formattedText)
        }
        return strings

    }

    override fun trimStringToWidth(string: String, maxWidth: Int, reverse: Boolean): String {
        return textRenderer.trimToWidth(string, maxWidth, reverse)
    }
}
