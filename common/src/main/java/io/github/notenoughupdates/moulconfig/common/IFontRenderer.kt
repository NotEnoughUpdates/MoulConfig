package io.github.notenoughupdates.moulconfig.common

import io.github.notenoughupdates.moulconfig.common.text.StructuredText

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 */
interface IFontRenderer {
    val height: Int
    fun getStringWidth(string: StructuredText): Int
    fun getStringWidth(string: String): Int = getStringWidth(StructuredText.of(string))
    fun getCharWidth(char: Char): Int
    fun splitText(text: StructuredText, width: Int): List<StructuredText>
    fun splitLines(text: StructuredText): List<StructuredText> {
        return splitText(text, Int.MAX_VALUE)
    }

    fun trimStringToWidth(string: String, width: Int) = trimStringToWidth(string, width, false)
    fun trimStringToWidth(string: String, width: Int, reverse: Boolean): String

}
