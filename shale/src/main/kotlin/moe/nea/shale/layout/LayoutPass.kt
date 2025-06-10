package moe.nea.shale.layout

import moe.nea.shale.render.GraphicsContext

sealed interface LayoutPass {
    val layoutContext: LayoutContext
    fun visitChild(element: Element) {
        element.visit(this)
    }

    data class Adopt(override val layoutContext: LayoutContext) : LayoutPass
    data class Reset(override val layoutContext: LayoutContext) : LayoutPass
    data class Fit(val axis: LayoutAxis, override val layoutContext: LayoutContext) : LayoutPass
    data class Grow(val axis: LayoutAxis, override val layoutContext: LayoutContext) : LayoutPass
    data class Wrap(override val layoutContext: LayoutContext) : LayoutPass
    data class RelativePosition(override val layoutContext: LayoutContext) : LayoutPass
    data class AbsolutePosition(override val layoutContext: LayoutContext) : LayoutPass

    data class Render(val graphicsContext: GraphicsContext) : LayoutPass {
        override val layoutContext: LayoutContext get() = graphicsContext
    }
}
