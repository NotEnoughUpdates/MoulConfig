package moe.nea.shale.render.minecraft

import moe.nea.shale.layout.MeasuredText
import moe.nea.shale.layout.Size

data class MinecraftWrappedText(
    val lines: List<String>,
    override val size: Size
) : MeasuredText
