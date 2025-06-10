package moe.nea.shale.layout

import moe.nea.shale.render.GraphicsContext

sealed interface LayoutPass {
    fun visitChild(element: Element) {
        element.visit(this)
    }

    object Adopt : LayoutPass

    // TODO: width, height passes separation, first layout width, then wrap, then height, then relative position, then absolute position
    object Fit : LayoutPass
    object Grow : LayoutPass

    data class Render(val graphicsContext: GraphicsContext) : LayoutPass
}
