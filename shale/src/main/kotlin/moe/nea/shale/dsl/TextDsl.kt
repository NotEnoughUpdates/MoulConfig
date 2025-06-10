package moe.nea.shale.dsl

import moe.nea.shale.layout.TextElement
import java.awt.Color

class TextDsl : ShaleBuilder() {
    val element = TextElement()

    fun text(text: String) {
        element.text = text
    }

    fun color(color: Color) {
        element.color = color
    }

    override fun compile(): TextElement {
        markFinalized()
        return element
    }
}

@ShaleDsl
fun HasChildren<in TextElement>.text(text: String = "", builder: TextDsl.() -> Unit = {}) {
    add(TextDsl().also { it.text(text) }.also(builder).compile())
}
