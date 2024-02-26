package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiUtilRenderComponents
import net.minecraft.util.ChatComponentText
import java.util.regex.Pattern


class ForgeFontRenderer(val font: FontRenderer) : IFontRenderer {
    override val height: Int
        get() = font.FONT_HEIGHT

    override fun getStringWidth(string: String): Int {
        return font.getStringWidth(string)
    }

    override fun getCharWidth(char: Char): Int {
        return font.getCharWidth(char)
    }

    companion object {
        private val colorPattern: Pattern = Pattern.compile("§[a-f0-9r]")
    }

    override fun splitText(text: String, width: Int): List<String> {
        val iChatComponents =
            GuiUtilRenderComponents.splitText(ChatComponentText(text), width, font, false, false)
        var lastFormat = "§r"
        val strings: MutableList<String> = ArrayList(iChatComponents.size)
        for (iChatComponent in iChatComponents) {
            val formattedText = lastFormat + iChatComponent.formattedText.replace("^((§.)*) *".toRegex(), "$1")
            strings.add(formattedText)
            val matcher = colorPattern.matcher(formattedText)
            while (matcher.find()) {
                lastFormat = matcher.group(0)
            }
        }
        return strings
    }

    override fun trimStringToWidth(string: String, maxWidth: Int, reverse: Boolean): String {
        return font.trimStringToWidth(string, maxWidth, reverse)
    }

}
