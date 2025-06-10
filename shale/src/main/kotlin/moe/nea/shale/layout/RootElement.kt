package moe.nea.shale.layout

import moe.nea.shale.render.GraphicsContext

class RootElement : BoxElement() {
    /**
     * Emit a relayout
     */
    fun relayout() {
        val layoutPasses = listOf<LayoutPass>(
            LayoutPass.Adopt,
            LayoutPass.Fit,
            LayoutPass.Grow,
            LayoutPass.RelativePosition,
            LayoutPass.AbsolutePosition,
            // TODO: text wrap, position
        )
        layoutPasses.forEach {
            it.visitChild(this)
        }
    }

    fun render(graphicsContext: GraphicsContext) {
        LayoutPass.Render(graphicsContext).visitChild(this)
    }
}
