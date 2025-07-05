package io.github.notenoughupdates.moulconfig.platform

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.ChatMessages
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting

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
        val lines = ChatMessages.breakRenderedChatMessageLines(Text.literal(text), width, MinecraftClient.getInstance().textRenderer)
        val strings: MutableList<String> = ArrayList(lines.size)

        for (line in lines) {
            var newLine = ""
            var lastColor: TextColor? = null
            var lastFormatting = ""
            line.accept { index, style, codePoint ->
                val color = style.color
                if (color != lastColor) {
                    lastColor = color
                    lastFormatting = ""
                    if (color != null) {
                        newLine += color.toChatFormatting()
                    }
                }
                val newFormatting = when {
                    style.isBold -> "§l"
                    style.isItalic -> "§o"
                    style.isUnderlined -> "§n"
                    style.isStrikethrough -> "§m"
                    style.isObfuscated -> "§k"
                    else -> ""
                }

                if (newFormatting != lastFormatting) {
                    lastFormatting = newFormatting
                    newLine += newFormatting
                }
                newLine += codePoint.toChar()
                true
            }
            strings.add(newLine)
        }
        return strings
    }


    override fun trimStringToWidth(string: String, maxWidth: Int, reverse: Boolean): String {
        return textRenderer.trimToWidth(string, maxWidth, reverse)
    }

    companion object {
        private fun TextColor.toChatFormatting(): Formatting? {
            return textColorLUT[this.rgb]
        }

        private val textColorLUT = Formatting.entries
            .mapNotNull { formatting -> formatting.colorValue?.let { it to formatting } }
            .toMap()
    }
}
