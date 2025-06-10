package moe.nea.shale.dsl

import moe.nea.shale.layout.BoxElement

fun HasChildren<in BoxElement>.box(builder: ShaleBoxBuilder.() -> Unit) {
    add(ShaleBoxBuilder().also(builder).compile())
}
