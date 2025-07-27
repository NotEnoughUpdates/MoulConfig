package io.github.notenoughupdates.moulconfig.platform

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.ChatMessages

class ModernFontRenderer(val textRenderer: TextRenderer) :
    IFontRenderer {
    override val height: Int
        get() = textRenderer.fontHeight

    override fun getStringWidth(string: StructuredText): Int {
        return textRenderer.getWidth(MoulConfigText.unwrap(string))
    }

    override fun getCharWidth(char: Char): Int {
        return textRenderer.getWidth(char + "")
    }

    override fun splitText(text: StructuredText, width: Int): List<StructuredText> {
        val lines = ChatMessages.breakRenderedChatMessageLines(MoulConfigText.unwrap(text), width, textRenderer)
        val strings: MutableList<StructuredText> = ArrayList(lines.size)
        for (iChatComponent in lines) {
            var formattedText = ""
            iChatComponent.accept { i, style, j ->
                formattedText += j.toChar() // TODO: Implement a proper reconstitution
                true
            }
            strings.add(StructuredText.of(formattedText))
        }
        return strings

    }

    override fun splitLines(text: StructuredText): List<StructuredText> {
        return listOf(text) // TODO: this is very much wrong
    }

    override fun trimStringToWidth(string: String, width: Int, reverse: Boolean): String {
        return textRenderer.trimToWidth(string, width, reverse)
    }


}
