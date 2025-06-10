package moe.nea.shale.render.awt

import moe.nea.shale.layout.Area
import moe.nea.shale.layout.Position
import moe.nea.shale.layout.Size
import java.awt.font.TextLayout

data class AwtWrappedTextLine(val layout: TextLayout, val position: Position, val ascent: Int) {
    val size: Size = run {
        val bounds = layout.bounds
        Size(bounds.width.toInt(), bounds.height.toInt())
    }
    val area = Area(position, size)
}
