package moe.nea.shale.layout

import moe.nea.shale.render.GraphicsContext

sealed interface LayoutPass {
    fun visitChild(element: Element) {
        element.visit(this)
    }

    object Adopt : LayoutPass

    object Reset : LayoutPass
    data class Fit(val axis: LayoutAxis) : LayoutPass
    data class Grow(val axis: LayoutAxis) : LayoutPass
    object RelativePosition : LayoutPass
    object AbsolutePosition : LayoutPass

    data class Render(val graphicsContext: GraphicsContext) : LayoutPass
}
