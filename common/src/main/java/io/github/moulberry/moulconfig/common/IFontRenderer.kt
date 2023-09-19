package io.github.moulberry.moulconfig.common

interface IFontRenderer {
    val height: Int
    fun getStringWidth(string: String): Int
    fun getCharWidth(char: Char): Int
    fun splitText(text: String, width: Int): List<String>
    fun trimStringToWidth(string: String, maxWidth: Int, reverse: Boolean): String
    fun trimStringToWidth(string: String, maxWidth: Int): String {
        return trimStringToWidth(string, maxWidth, false)
    }

    fun drawString(text: String, x: Int, y: Int, color: Int, shadow: Boolean)

}
