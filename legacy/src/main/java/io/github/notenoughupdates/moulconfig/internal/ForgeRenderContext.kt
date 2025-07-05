package io.github.notenoughupdates.moulconfig.internal

import io.github.notenoughupdates.moulconfig.common.*
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
import java.util.function.Consumer

class ForgeRenderContext : RenderContext {
    override fun pushMatrix() {
        GlStateManager.pushMatrix()
    }

    override fun popMatrix() {
        GlStateManager.popMatrix()
    }

    override fun translate(x: Float, y: Float) {
        GlStateManager.translate(x, y, 0F)
    }

    override fun scale(x: Float, y: Float) {
        GlStateManager.scale(x, y, 1f)
    }

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return Mouse.isButtonDown(mouseButton)
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return Keyboard.isKeyDown(keyboardKey)
    }

    override fun drawString(renderer: IFontRenderer, text: String, x: Int, y: Int, color: Int, shadow: Boolean) {
        (renderer as ForgeFontRenderer).font.drawString(text, x.toFloat(), y.toFloat(), color, shadow)
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

    fun applyGlobalColor(color: Int) {
        GlStateManager.color(ColourUtil.unpackARGBRedF(color), ColourUtil.unpackARGBGreenF(color), ColourUtil.unpackARGBBlueF(color), ColourUtil.unpackARGBAlphaF(color))
    }

    override fun drawColoredTriangles(color: Int, vararg coordinates: Float) {
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        worldrenderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION)

        applyGlobalColor(color)
        require(coordinates.size % 6 == 0)
        for (i in 0 until (coordinates.size / 2)) {
            worldrenderer.pos(coordinates[i * 2].toDouble(), coordinates[i * 2 + 1].toDouble(), 0.0).endVertex()
        }

        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun drawGradientRect(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        startColor: Int,
        endColor: Int
    ) {
        RenderUtils.drawGradientRect(
            0,
            left,
            top,
            right,
            bottom,
            startColor,
            endColor
        )
    }

    override fun invertedRect(left: Float, top: Float, right: Float, bottom: Float, additiveColor: Int) {
        GlStateManager.enableColorLogic()
        GlStateManager.colorLogicOp(GL11.GL_OR_REVERSE)
        GlStateManager.disableTexture2D()
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        applyGlobalColor(additiveColor)
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(right.toDouble(), top.toDouble(), 0.0).endVertex()
        worldrenderer.pos(left.toDouble(), top.toDouble(), 0.0).endVertex()
        worldrenderer.pos(left.toDouble(), bottom.toDouble(), 0.0).endVertex()
        worldrenderer.pos(right.toDouble(), bottom.toDouble(), 0.0).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableColorLogic()
    }

    override fun drawTexturedTintedRect(texture: MyResourceLocation, x: Float, y: Float, width: Float, height: Float, u1: Float, v1: Float, u2: Float, v2: Float, color: Int, filter: TextureFilter) {
        FilterAssertionCache.assertTextureFilter(texture, filter)
        applyGlobalColor(color)
        Minecraft.getMinecraft().textureManager.bindTexture(ForgeMinecraft.fromMyResourceLocation(texture))
        RenderUtils.drawTexturedRect(
            x, y,
            width, height,
            u1, u2, v1, v2,
            when (filter) {
                TextureFilter.LINEAR -> GL11.GL_LINEAR
                TextureFilter.NEAREST -> GL11.GL_NEAREST
            }
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

    override fun pushScissor(left: Int, top: Int, right: Int, bottom: Int) { // TODO: make this translate (by reading out the matrix state, sadly)
        GlScissorStack.push(
            left,
            top,
            right,
            bottom,
            ScaledResolution(Minecraft.getMinecraft()),
            false
        )
    }

    override fun pushRawScissor(left: Int, top: Int, right: Int, bottom: Int) {
        GlScissorStack.push(
            left,
            top,
            right,
            bottom,
            ScaledResolution(Minecraft.getMinecraft()),
            true
        )
    }

    override fun popScissor() {
        GlScissorStack.pop(ScaledResolution(Minecraft.getMinecraft()))
    }

    override fun assertNoScissors() {
        if (!GlScissorStack.isEmpty())
            Warnings.warn("no scissor assertion failed", 4)
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


    override fun drawTooltipNow(x: Int, y: Int, tooltipLines: List<String>) {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val width = scaledResolution.scaledWidth
        val height = scaledResolution.scaledHeight
        val mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth
        val mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1
        TextRenderUtils.drawHoveringText(
            tooltipLines, mouseX, mouseY,
            width, height, -1, Minecraft.getMinecraft().fontRendererObj
        )
    }

    override fun drawOnTop(layer: Layer, scissorBehaviour: RenderContext.ScissorBehaviour, later: Consumer<RenderContext>) {
        pushMatrix()
        if (scissorBehaviour == RenderContext.ScissorBehaviour.ESCAPE) {
            pushRawScissor(0, 0, minecraft.scaledWidth, minecraft.scaledHeight)
        }
        GlStateManager.translate(0F, 0F, layer.sortIndex.toFloat())
        later.accept(this)
        if (scissorBehaviour == RenderContext.ScissorBehaviour.ESCAPE) {
            popScissor()
        }
        popMatrix()
    }

    override fun renderExtraLayers() {
        // Left blank: [drawOnTop] renders directly.
    }
}
