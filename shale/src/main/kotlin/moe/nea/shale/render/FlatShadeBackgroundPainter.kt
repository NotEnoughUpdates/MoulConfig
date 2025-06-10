package moe.nea.shale.render

import moe.nea.shale.layout.Area
import java.awt.Color

class FlatShadeBackgroundPainter(val color: Color) : BackgroundPainter {
    override fun paint(context: GraphicsContext, area: Area) {
        context.rect(area, color)
    }
}
