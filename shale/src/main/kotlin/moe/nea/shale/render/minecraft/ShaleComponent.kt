package moe.nea.shale.render.minecraft

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import moe.nea.shale.layout.RootElement
import moe.nea.shale.layout.Size
import moe.nea.shale.layout.Sizing

class ShaleComponent(val tree: RootElement) : GuiComponent() {
    override fun getWidth(): Int {
        return 0
    }

    override fun getHeight(): Int {
        return 0
    }

    override fun render(context: GuiImmediateContext) {
        tree.sizing = Sizing.Fixed(Size(context.width, context.height))
        val context = MinecraftGraphicsContext(context.renderContext)
        tree.relayout(context)
        tree.render(context)
    }

    fun openScreen() {
        IMinecraft.instance.openWrappedScreen(this)
    }
}
