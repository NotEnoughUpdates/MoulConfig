package moe.nea.shale.render

import moe.nea.shale.layout.Area
import java.awt.Color
import java.awt.Graphics

class AwtGraphicsContext(val g: Graphics) : GraphicsContext {
    override fun rect(area: Area, color: Color) {
        g.color = color
        g.fillRect(area.x, area.y, area.width, area.height)
    }

    override fun text(area: Area, color: Color, text: String) {
        TODO("Not yet implemented")
    }
}
