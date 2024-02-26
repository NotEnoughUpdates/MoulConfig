package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import io.github.notenoughupdates.moulconfig.common.RenderContext
import juuxel.libninepatch.ContextualTextureRenderer

object NinePatchRenderer : ContextualTextureRenderer<MyResourceLocation, RenderContext> {
    override fun draw(
        texture: MyResourceLocation,
        context: RenderContext,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        u1: Float,
        v1: Float,
        u2: Float,
        v2: Float
    ) {
        context.bindTexture(texture)
        context.drawTexturedRect(
            x.toFloat(),
            y.toFloat(),
            width.toFloat(),
            height.toFloat(),
            u1,
            v1,
            u2,
            v2
        )
    }
}