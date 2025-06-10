package moe.nea.shale.render

import moe.nea.shale.layout.Area
import moe.nea.shale.layout.LayoutContext
import moe.nea.shale.layout.MeasuredText
import moe.nea.shale.layout.Position
import java.awt.Color

interface GraphicsContext : LayoutContext {
    fun rect(area: Area, color: Color)
    fun text(area: Area, color: Color, text: String) {
        text(area.position, color, measureWrappedText(text, area.width))
    }

    fun text(position: Position, color: Color, measuredText: MeasuredText)
}
