package io.github.notenoughupdates.moulconfig.gui

import io.github.moulberry.moulconfig.gui.GuiContext
import io.github.moulberry.moulconfig.gui.GuiImmediateContext
import io.github.moulberry.moulconfig.gui.KeyboardEvent
import io.github.moulberry.moulconfig.gui.MouseEvent
import io.github.notenoughupdates.moulconfig.platform.ModernRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class GuiComponentWrapper(
    val context: GuiContext,
    label: Text = Text.literal("")
) : Screen(label) {
    private fun createContext(drawContext: DrawContext? = null): GuiImmediateContext {
        val mouse = MinecraftClient.getInstance().mouse
        val window = client!!.window
        val x = (mouse.x * window.scaledWidth.toDouble() / window.width.toDouble()).toInt()
        val y = (mouse.y * window.scaledHeight.toDouble() / window.height.toDouble()).toInt()
        return GuiImmediateContext(
            ModernRenderContext(
                drawContext ?: DrawContext(
                    MinecraftClient.getInstance(),
                    MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
                )
            ),
            0, 0,
            window.scaledWidth,
            window.scaledHeight,
            x, y, x, y
        )
    }

    override fun render(drawContext: DrawContext?, i: Int, j: Int, f: Float) {
        super.render(drawContext, i, j, f)
        val ctx = createContext(drawContext)
        context.root.render(ctx)
        ctx.renderContext.doDrawTooltip()
    }

    override fun charTyped(c: Char, i: Int): Boolean {
        context.root.keyboardEvent(KeyboardEvent.CharTyped(c), createContext())
        return true
    }

    override fun keyPressed(i: Int, j: Int, k: Int): Boolean {
        context.root.keyboardEvent(KeyboardEvent.KeyPressed(i, true), createContext())
        return true
    }

    override fun keyReleased(i: Int, j: Int, k: Int): Boolean {
        context.root.keyboardEvent(KeyboardEvent.KeyPressed(i, false), createContext())
        return true
    }

    override fun mouseMoved(d: Double, e: Double) {
        val ctx = createContext()
        val di = d.toInt()
        val ei = e.toInt()
        val event = MouseEvent.Move(d.toFloat() - ctx.mouseX, e.toFloat() - ctx.mouseY)
        val repositionedContext = ctx.copy(mouseX = di, mouseY = ei, absoluteMouseX = di, absoluteMouseY = ei)
        context.root.mouseEvent(event, repositionedContext)
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
        context.root.mouseEvent(MouseEvent.Click(i, true), createContext())
        return true
    }

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean {
        context.root.mouseEvent(MouseEvent.Click(i, false), createContext())
        return true
    }

    override fun mouseScrolled(d: Double, e: Double, f: Double): Boolean {
        context.root.mouseEvent(MouseEvent.Scroll(f.toFloat()), createContext())
        return true
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return true
    }
}
