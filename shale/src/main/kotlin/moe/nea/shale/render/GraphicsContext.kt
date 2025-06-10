package moe.nea.shale.render

import moe.nea.shale.layout.Area
import java.awt.Color

interface GraphicsContext {
    fun rect(area: Area, color: Color)
    fun text(area: Area, color: Color, text: String)
}
