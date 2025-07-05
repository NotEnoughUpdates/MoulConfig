package io.github.notenoughupdates.moulconfig.gui

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

open class GuiElementWrapper(
    val guiElement: GuiElement,
) : Screen(Text.literal("")) {
    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        guiElement.render()
    }

    override fun charTyped(c: Char, i: Int): Boolean {
        guiElement.keyboardInput(KeyboardEvent.CharTyped(c))
        return true
    }

    override fun keyPressed(i: Int, j: Int, k: Int): Boolean {
        if (guiElement.keyboardInput(KeyboardEvent.KeyPressed(i, true)))
            return true
        if (i == 256) {
            close()
            return true
        }
        return false
    }

    override fun keyReleased(i: Int, j: Int, k: Int): Boolean {
        return guiElement.keyboardInput(KeyboardEvent.KeyPressed(i, false))
    }

    override fun mouseMoved(d: Double, e: Double) {
        val di = d.toInt()
        val ei = e.toInt()
        val event = MouseEvent.Move(d.toFloat() - IMinecraft.instance.mouseX, e.toFloat() - IMinecraft.instance.mouseY)
        guiElement.mouseInput(di, ei, event)
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
        return guiElement.mouseInput(d.toInt(), e.toInt(), MouseEvent.Click(i, true))
    }

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean {
        return guiElement.mouseInput(d.toInt(), e.toInt(), MouseEvent.Click(i, false))
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        return guiElement.mouseInput(
            mouseX.toInt(),
            mouseY.toInt(),
            MouseEvent.Scroll(verticalAmount.toFloat()),
        )
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return true // TODO?
    }

}
