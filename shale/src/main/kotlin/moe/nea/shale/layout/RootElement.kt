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
    fun relayout(layoutContext: LayoutContext) {
        val layoutPasses = listOf<LayoutPass>(
            LayoutPass.Adopt(layoutContext),
            LayoutPass.Reset(layoutContext),
            LayoutPass.Fit(textFlowAxis, layoutContext),
            LayoutPass.Grow(textFlowAxis, layoutContext),
            LayoutPass.Wrap(layoutContext),
            LayoutPass.Fit(textFlowAxis.cross, layoutContext),
            LayoutPass.Grow(textFlowAxis.cross, layoutContext),
            LayoutPass.RelativePosition(layoutContext),
            LayoutPass.AbsolutePosition(layoutContext),
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
