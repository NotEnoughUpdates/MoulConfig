package moe.nea.shale.render.awt

import moe.nea.shale.layout.Area
import moe.nea.shale.layout.MeasuredText
import moe.nea.shale.layout.Position
import moe.nea.shale.render.GraphicsContext
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.font.LineBreakMeasurer
import java.text.AttributedString

class AwtGraphicsContext(val g: Graphics) : GraphicsContext {
    val g2d = g as Graphics2D
    override fun rect(area: Area, color: Color) {
        g.color = color
        g.fillRect(area.x, area.y, area.width, area.height)
    }

    override fun text(position: Position, color: Color, measuredText: MeasuredText) {
        measuredText as AwtMeasuredText
        g.color = color
        measuredText.lines.forEach {
            val pos = it.position + position
            it.layout.draw(g2d, pos.x.toFloat(), pos.y.toFloat() + it.ascent)
        }
    }

    override fun measureWrappedText(text: String, width: Int): MeasuredText {
        val measurer = LineBreakMeasurer(AttributedString(text).iterator, g2d.fontRenderContext)
        measurer.position = 0
        var drawPositionY = 0
        val lines = mutableListOf<AwtWrappedTextLine>()
        while (measurer.position < text.length) {
            val layout = measurer.nextLayout(width.toFloat())
            val drawPositionX = if (layout.isLeftToRight) 0 else (width - layout.advance).toInt()
            lines.add(
                AwtWrappedTextLine(
                    layout,
                    Position(drawPositionX, drawPositionY),
                    layout.ascent.toInt()
                )
            )
            drawPositionY += layout.ascent.toInt()
            drawPositionY += layout.descent.toInt()
            drawPositionY += layout.leading.toInt()
        }
        return AwtMeasuredText(lines)
    }

    override fun measureUnwrappedText(text: String): Int {
        return g.fontMetrics.stringWidth(text)
    }
}
