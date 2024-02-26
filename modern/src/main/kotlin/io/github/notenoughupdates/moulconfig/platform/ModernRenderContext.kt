package io.github.notenoughupdates.moulconfig.platform

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.common.RenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.util.*

class ModernRenderContext(val drawContext: DrawContext) : RenderContext {
    val mouse = MinecraftClient.getInstance().mouse
    val window = MinecraftClient.getInstance().window
    val scissors = Stack<Scissor>()

    data class Scissor(val left: Double, val top: Double, val right: Double, val bottom: Double)

    override fun disableDepth() {
        RenderSystem.disableDepthTest()
    }

    override fun enableDepth() {
        RenderSystem.enableDepthTest()
    }

    fun refreshScissors() {
        if (scissors.isEmpty()) {
            GL11.glScissor(0, 0, window.framebufferWidth, window.framebufferHeight)
            return
        }
        var l = 0.0
        var t = 0.0
        var r = window.framebufferWidth * window.scaleFactor
        var b = window.framebufferHeight * window.scaleFactor
        for (frame in scissors) {
            l = l.coerceAtLeast(frame.left * window.scaleFactor)
            t = t.coerceAtLeast(frame.top * window.scaleFactor)
            r = r.coerceAtMost(frame.right * window.scaleFactor)
            b = b.coerceAtMost(frame.bottom * window.scaleFactor)
        }
        GL11.glScissor(l.toInt(), t.toInt(), r.toInt(), b.toInt())
    }

    override fun refreshScissor() {
        refreshScissors()
    }

    override fun disableScissor() {
        GL11.glScissor(0, 0, window.framebufferWidth, window.framebufferHeight)
    }

    override fun pushMatrix() {
        drawContext.matrices.push()
    }

    override fun popMatrix() {
        drawContext.matrices.pop()
    }

    override fun translate(x: Float, y: Float, z: Float) {
        drawContext.matrices.translate(x, y, z)
    }

    override fun scale(x: Float, y: Float, z: Float) {
        drawContext.matrices.scale(x, y, z)
    }

    override fun color(r: Float, g: Float, b: Float, a: Float) {
        drawContext.setShaderColor(r, g, b, a)
    }

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return GLFW.glfwGetMouseButton(window.handle, mouseButton) == GLFW.GLFW_PRESS
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return InputUtil.isKeyPressed(window.handle, keyboardKey)
    }

    override fun drawTriangles(vararg coordinates: Float) {
        val tess = Tessellator.getInstance()
        val buffer = tess.buffer
        val matrix = drawContext.matrices.peek().positionMatrix
        RenderSystem.enableBlend()
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION)

        require(coordinates.size % 6 == 0)
        for (i in 0 until (coordinates.size / 2)) {
            buffer.vertex(matrix, coordinates[i * 2], coordinates[i * 2 + 1], 0.0F).next()
        }

        tess.draw()
    }

    override fun drawString(
        fontRenderer: IFontRenderer?,
        text: String?,
        x: Int,
        y: Int,
        color: Int,
        shadow: Boolean
    ): Int {
        return drawContext.drawText((fontRenderer as ModernFontRenderer).textRenderer, text, x, y, color, shadow)
    }

    override fun drawColoredRect(left: Float, top: Float, right: Float, bottom: Float, color: Int) {
        drawContext.fill(left.toInt(), top.toInt(), right.toInt(), bottom.toInt(), color)
    }

    override fun invertedRect(left: Float, top: Float, right: Float, bottom: Float) {
        val tess = Tessellator.getInstance()
        val buffer = tess.buffer
        val matrix = drawContext.matrices.peek().positionMatrix
        RenderSystem.setShaderColor(0F, 0F, 1f, 1f)
        RenderSystem.setShader(GameRenderer::getPositionProgram)
        RenderSystem.enableColorLogicOp()
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE)
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)
        buffer.vertex(matrix, left, bottom, 0F).next()
        buffer.vertex(matrix, right, bottom, 0F).next()
        buffer.vertex(matrix, right, top, 0F).next()
        buffer.vertex(matrix, left, top, 0F).next()
        BufferRenderer.drawWithGlobalProgram(buffer.end())
        RenderSystem.disableColorLogicOp()
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    }

    override fun drawTexturedRect(x: Float, y: Float, width: Float, height: Float) {
        RenderSystem.setShaderTexture(0, ModernMinecraft.boundTexture!!)
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        val matrix4f: Matrix4f = drawContext.matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(matrix4f, x, y, 0F).texture(0F, 0F).next()
        bufferBuilder.vertex(matrix4f, x, y + height, 0f).texture(0F, 1F).next()
        bufferBuilder.vertex(matrix4f, x + width, y + height, 0f).texture(1F, 1F).next()
        bufferBuilder.vertex(matrix4f, x + width, y, 0F).texture(1F, 0F).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
    }

    override fun drawDarkRect(x: Int, y: Int, width: Int, height: Int, shadow: Boolean) {
        val main: Int = -0x1000000 or 0x202026
        val light = -0xcfcfca
        val dark = -0xefefea
        val shadow = true
        drawContext.fill(x, y, x + 1, y + height, light) //Left
        drawContext.fill(x + 1, y, x + width, y + 1, light) //Top
        drawContext.fill(x + width - 1, y + 1, x + width, y + height, dark) //Right
        drawContext.fill(x + 1, y + height - 1, x + width - 1, y + height, dark) //Bottom
        drawContext.fill(x + 1, y + 1, x + width - 1, y + height - 1, main) //Middle
        if (shadow) {
            drawContext.fill(x + width, y + 2, x + width + 2, y + height + 2, 0x70000000) //Right shadow
            drawContext.fill(x + 2, y + height, x + width, y + height + 2, 0x70000000) //Bottom shadow
        }
        // TODO: do shadow
    }

    override fun drawGradientRect(
        zLevel: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        startColor: Int,
        endColor: Int
    ) {
        drawContext.fillGradient(RenderLayer.getGui(), left, top, right, bottom, startColor, endColor, zLevel)
    }

    override fun pushScissor(left: Int, top: Int, right: Int, bottom: Int) {
        scissors.add(Scissor(left.toDouble(), top.toDouble(), right.toDouble(), bottom.toDouble()))
        refreshScissors()
    }

    override fun popScissor() {
        scissors.removeLast()
        refreshScissors()
    }

    override fun clearScissor() {
        scissors.clear()
        refreshScissors()
    }

    override fun renderItemStack(itemStack: IItemStack, x: Int, y: Int, overlayText: String?) {
        val item = (itemStack as ModernItemStack).backing
        drawContext.drawItem(item, x, y)
        drawContext.drawItemInSlot(
            MinecraftClient.getInstance().textRenderer,
            item,
            x,
            y,
            overlayText ?: ""
        )
    }

    var scheduledTooltip: List<String>? = null

    override fun scheduleDrawTooltip(tooltipLines: MutableList<String>) {
        scheduledTooltip = tooltipLines
    }

    override fun doDrawTooltip() {
        if (scheduledTooltip != null) {
            drawContext.drawTooltip(
                MinecraftClient.getInstance().textRenderer,
                scheduledTooltip!!.map { Text.literal(it) },
                // TODO: improve this somewhat
                (MinecraftClient.getInstance().mouse.x / MinecraftClient.getInstance().window.scaleFactor).toInt(),
                (MinecraftClient.getInstance().mouse.y / MinecraftClient.getInstance().window.scaleFactor).toInt(),
            )
        }
    }

}
