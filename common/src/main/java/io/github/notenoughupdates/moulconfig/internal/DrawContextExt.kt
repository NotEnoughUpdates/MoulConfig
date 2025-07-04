package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.RenderContext

object DrawContextExt {
    @JvmOverloads
    @JvmStatic
    fun RenderContext.drawStringCenteredScalingDownWithMaxWidth(
        text: String,
        centerX: Int,
        centerY: Int,
        maxWidth: Int,
        color: Int,
        shadow: Boolean = false,
        fr: IFontRenderer = minecraft.defaultFontRenderer,
    ) {
        pushMatrix()
        val width = fr.getStringWidth(text)
        val factor = (maxWidth / width.toFloat()).coerceAtMost(1f)
        translate(centerX.toFloat(), centerY.toFloat())
        scale(factor, factor)
        drawString(fr, text, -width / 2, -fr.height / 2, color, shadow)
        popMatrix()
    }
}
