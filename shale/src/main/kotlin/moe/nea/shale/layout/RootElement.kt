package moe.nea.shale.layout

import moe.nea.shale.render.GraphicsContext

class RootElement : BoxElement() {
    /**
     * The primary direction of text flow. This is used to split up grow / fit phases into one along the text flow and one across text flow, with a text wrap phase in the middle.
     * This is needed to support text wrapping increasing the preferred cross size depending on the along size.
     */
    var textFlowAxis = LayoutAxis.HORIZONTAL

    /**
     * Emit a relayout
     */
    fun relayout() {
        val layoutPasses = listOf<LayoutPass>(
            LayoutPass.Adopt,
            LayoutPass.Reset,
            LayoutPass.Fit(textFlowAxis),
            LayoutPass.Grow(textFlowAxis),
            LayoutPass.Fit(textFlowAxis.cross),
            LayoutPass.Grow(textFlowAxis.cross),
            LayoutPass.RelativePosition,
            LayoutPass.AbsolutePosition,
            // TODO: text wrap
        )
        layoutPasses.forEach {
            it.visitChild(this)
        }
    }

    fun render(graphicsContext: GraphicsContext) {
        LayoutPass.Render(graphicsContext).visitChild(this)
    }
}
