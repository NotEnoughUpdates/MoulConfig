package io.github.notenoughupdates.moulconfig.platform

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.StringVisitable
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.util.*

class ModernFontRenderer(val textRenderer: TextRenderer) :
    IFontRenderer {
    override val height: Int
        get() = textRenderer.fontHeight

    override fun getStringWidth(string: StructuredText): Int {
        return textRenderer.getWidth(MoulConfigText.unwrap(string))
    }

    override fun getStringWidth(string: String): Int {
        return textRenderer.getWidth(string)
    }

    override fun getCharWidth(char: Char): Int {
        return textRenderer.getWidth(char + "")
    }

    override fun splitText(text: StructuredText, width: Int): List<StructuredText> {
        val lines = mutableListOf<StructuredText>()
        textRenderer.textHandler.wrapLines(MoulConfigText.unwrap(text), width, Style.EMPTY) { visitable, lastLineWrapped ->
            val text = Text.empty()
            visitable.visit(StringVisitable.StyledVisitor<Unit> { arg, string ->
                text.append(Text.literal(string).setStyle(arg))
                Optional.empty()
            }, Style.EMPTY)
            lines.add(MoulConfigText.wrap(text))
        }
        return lines
    }

    override fun trimStringToWidth(string: String, width: Int, reverse: Boolean): String {
        return textRenderer.trimToWidth(string, width, reverse)
    }
}
