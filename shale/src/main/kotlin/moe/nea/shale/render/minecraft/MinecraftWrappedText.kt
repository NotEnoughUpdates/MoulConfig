package moe.nea.shale.render.minecraft

import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import moe.nea.shale.layout.MeasuredText
import moe.nea.shale.layout.Size

data class MinecraftWrappedText(
    val lines: List<StructuredText>,
    override val size: Size
) : MeasuredText
