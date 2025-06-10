package moe.nea.shale.render.awt

import moe.nea.shale.layout.Area
import moe.nea.shale.layout.MeasuredText
import moe.nea.shale.layout.Size

data class AwtMeasuredText(val lines: List<AwtWrappedTextLine>) : MeasuredText {
    val area = lines.fold(Area.ZERO_AT_ORIGIN) { a, b ->
        a.roughUnion(b.area)
    }

    override val size: Size = area.size
}
