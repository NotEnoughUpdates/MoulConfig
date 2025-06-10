package moe.nea.shale.render

import moe.nea.shale.layout.Area

interface BackgroundPainter {
    fun paint(context: GraphicsContext, area: Area)
}
