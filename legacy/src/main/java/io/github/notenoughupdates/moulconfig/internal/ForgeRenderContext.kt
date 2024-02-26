package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.common.RenderContext
import io.github.notenoughupdates.moulconfig.forge.ForgeItemStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11

class ForgeRenderContext : RenderContext {
    override fun pushMatrix() {
        GlStateManager.pushMatrix()
    }

    override fun popMatrix() {
        GlStateManager.popMatrix()
    }

    override fun disableDepth() {
        GlStateManager.disableDepth()
    }

    override fun enableDepth() {
        GlStateManager.enableDepth()
    }

    override fun translate(x: Float, y: Float, z: Float) {
        GlStateManager.translate(x, y, z)
    }

    override fun scale(x: Float, y: Float, z: Float) {
        GlStateManager.scale(x, y, z)
    }

    override fun color(r: Float, g: Float, b: Float, a: Float) {
        GlStateManager.color(r, g, b, a)
    }

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return Mouse.isButtonDown(mouseButton)
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return Keyboard.isKeyDown(keyboardKey)
    }

    override fun drawString(renderer: IFontRenderer, text: String, x: Int, y: Int, color: Int, shadow: Boolean): Int {
        return (renderer as ForgeFontRenderer).font.drawString(text, x.toFloat(), y.toFloat(), color, shadow)
    }


    override fun drawColoredRect(left: Float, top: Float, right: Float, bottom: Float, color: Int) {
        RenderUtils.drawGradientRect(
            0,
            left.toInt(),
            top.toInt(),
            right.toInt(),
            bottom.toInt(),
            color,
            color
        )
    }

    override fun drawTriangles(vararg coordinates: Float) {
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        worldrenderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION)

        require(coordinates.size % 6 == 0)
        for (i in 0 until (coordinates.size / 2)) {
            worldrenderer.pos(coordinates[i * 2].toDouble(), coordinates[i * 2 + 1].toDouble(), 0.0).endVertex()
        }

        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

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
        RenderUtils.drawGradientRect(
            zLevel,
            left,
            top,
            right,
            bottom,
            startColor,
            endColor
        )
    }

    override fun invertedRect(left: Float, top: Float, right: Float, bottom: Float) {
        GlStateManager.enableColorLogic()
        GlStateManager.colorLogicOp(GL11.GL_OR_REVERSE)
        GlStateManager.disableTexture2D()
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        GlStateManager.color(0.0f, 0.0f, 255.0f, 255.0f)
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(right.toDouble(), top.toDouble(), 0.0).endVertex()
        worldrenderer.pos(left.toDouble(), top.toDouble(), 0.0).endVertex()
        worldrenderer.pos(left.toDouble(), bottom.toDouble(), 0.0).endVertex()
        worldrenderer.pos(right.toDouble(), bottom.toDouble(), 0.0).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableColorLogic()
    }

    override fun drawTexturedRect(x: Float, y: Float, width: Float, height: Float) {
        RenderUtils.drawTexturedRect(
            x,
            y,
            width,
            height,
            GL11.GL_NEAREST
        )
    }

    override fun drawDarkRect(x: Int, y: Int, width: Int, height: Int, shadow: Boolean) {
        RenderUtils.drawFloatingRectDark(
            x,
            y,
            width,
            height,
            shadow
        )
    }

    override fun pushScissor(left: Int, top: Int, right: Int, bottom: Int) {
        GlScissorStack.push(
            left,
            top,
            right,
            bottom,
            ScaledResolution(Minecraft.getMinecraft())
        )
    }

    override fun popScissor() {
        GlScissorStack.pop(ScaledResolution(Minecraft.getMinecraft()))
    }

    override fun clearScissor() {
        GlScissorStack.clear()
    }

    override fun renderItemStack(itemStack: IItemStack, x: Int, y: Int, overlayText: String?) {
        val forgeStack = itemStack as ForgeItemStack
        val backing = forgeStack.backing
        val renderItem = Minecraft.getMinecraft().renderItem
        RenderHelper.enableGUIStandardItemLighting()
        renderItem.renderItemAndEffectIntoGUI(backing, x, y)
        if (overlayText != null) renderItem.renderItemOverlayIntoGUI(
            Minecraft.getMinecraft().fontRendererObj,
            backing,
            x,
            y,
            overlayText
        )
        RenderHelper.disableStandardItemLighting()
    }

    var scheduledTooltip: List<String>? = null

    override fun scheduleDrawTooltip(tooltipLines: List<String>) {
        scheduledTooltip = tooltipLines
    }

    override fun disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    override fun refreshScissor() {
        GlScissorStack.refresh(
            ScaledResolution(
                Minecraft.getMinecraft()
            )
        )
    }

    override fun doDrawTooltip() {
        if (scheduledTooltip != null) {
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val width = scaledResolution.scaledWidth
            val height = scaledResolution.scaledHeight
            val mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth
            val mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1
            TextRenderUtils.drawHoveringText(
                scheduledTooltip, mouseX, mouseY,
                width, height, -1, Minecraft.getMinecraft().fontRendererObj
            )
        }
    }
}
