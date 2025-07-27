package io.github.notenoughupdates.moulconfig.gui

import io.github.notenoughupdates.moulconfig.platform.ModernRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

/**
 * Wrapper for a [GuiContext]. Fabric specific equivalent of GuiScreenElementWrapperNew.
 */
open class GuiComponentWrapper(
    val context: GuiContext,
    label: Text = Text.literal("")
) : Screen(label) {
    init {
        context.setCloseRequestHandler(this::close)
    }

    open fun createContext(drawContext: DrawContext? = null): GuiImmediateContext {
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
            x, y, x, y, x.toFloat(), y.toFloat()
        )
    }

    override fun close() {
        if (context.onBeforeClose() == CloseEventListener.CloseAction.NO_OBJECTIONS_TO_CLOSE) {
            super.close()
        }
    }

    override fun removed() {
        context.onAfterClose()
    }


    override fun render(drawContext: DrawContext?, i: Int, j: Int, f: Float) {
        super.render(drawContext, i, j, f)
        val ctx = createContext(drawContext)
        context.root.render(ctx)
        ctx.renderContext.renderExtraLayers()
    }

    override fun charTyped(c: Char, i: Int): Boolean {
        return context.root.keyboardEvent(KeyboardEvent.CharTyped(c), createContext())
    }

    override fun keyPressed(i: Int, j: Int, k: Int): Boolean {
        if (context.root.keyboardEvent(KeyboardEvent.KeyPressed(i, true), createContext()))
            return true
        if (i == 256) {
            if (context.focusedElement != null) {
                context.focusedElement = null
            } else {
                close()
            }
            return true
        }
        return false
    }

    override fun keyReleased(i: Int, j: Int, k: Int): Boolean {
        return context.root.keyboardEvent(KeyboardEvent.KeyPressed(i, false), createContext())
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
        return context.root.mouseEvent(MouseEvent.Click(i, true), createContext())
    }

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean {
        return context.root.mouseEvent(MouseEvent.Click(i, false), createContext())
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        return context.root.mouseEvent(MouseEvent.Scroll(verticalAmount.toFloat()), createContext())
    }

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean {
        return true
    }
}
