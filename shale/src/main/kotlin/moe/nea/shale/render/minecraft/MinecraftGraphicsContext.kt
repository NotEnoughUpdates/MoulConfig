package moe.nea.shale.render.minecraft

import io.github.notenoughupdates.moulconfig.common.RenderContext
import moe.nea.shale.layout.Area
import moe.nea.shale.layout.MeasuredText
import moe.nea.shale.layout.Position
import moe.nea.shale.layout.Size
import moe.nea.shale.render.GraphicsContext
import java.awt.Color

class MinecraftGraphicsContext(
    val renderContext: RenderContext,
) : GraphicsContext {
    override fun rect(area: Area, color: Color) {
        renderContext.drawColoredRect(area.left.toFloat(), area.top.toFloat(), area.right.toFloat(), area.bottom.toFloat(), color.rgb)
    }

    override fun text(position: Position, color: Color, measuredText: MeasuredText) {
        measuredText as MinecraftWrappedText
        var offset = 0
        for (line in measuredText.lines) {
            renderContext.drawString(font, line, position.x, position.y + offset, color.rgb, false)
            offset += font.height
        }
    }

    val font = renderContext.minecraft.defaultFontRenderer

    override fun measureWrappedText(text: String, width: Int): MeasuredText {
        val lines = font.splitText(text, width)
        val size = Size(
            lines.maxOfOrNull { font.getStringWidth(it) } ?: 0,
            font.height * lines.size
        )
        return MinecraftWrappedText(lines, size)
    }

    override fun measureUnwrappedText(text: String): Int {
        return font.getStringWidth(text)
    }
}
