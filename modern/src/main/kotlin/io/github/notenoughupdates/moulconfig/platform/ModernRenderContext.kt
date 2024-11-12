package io.github.notenoughupdates.moulconfig.platform

import com.mojang.blaze3d.systems.RenderSystem
import io.github.notenoughupdates.moulconfig.common.DynamicTextureReference
import io.github.notenoughupdates.moulconfig.common.IFontRenderer
import io.github.notenoughupdates.moulconfig.common.IItemStack
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import io.github.notenoughupdates.moulconfig.common.RenderContext
import io.github.notenoughupdates.moulconfig.common.RenderContext.TextureFilter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.TriState
import net.minecraft.util.Util
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.awt.image.BufferedImage

class ModernRenderContext(val drawContext: DrawContext) : RenderContext {
    companion object {
        val RL = RenderLayer.of(
            "moulconfig_inverted_rect",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            RenderLayer.DEFAULT_BUFFER_SIZE,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                .program(RenderPhase.POSITION_PROGRAM)
                .colorLogic(RenderPhase.OR_REVERSE)
                .build(false)
        )
        val COLORED_TRIANGLES =
            RenderLayer.of(
                "mc_triangles",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.TRIANGLES,
                RenderLayer.DEFAULT_BUFFER_SIZE,
                false,
                false,
                RenderLayer.MultiPhaseParameters.builder()
                    // TODO: import mipmap settings
                    .program(RenderPhase.ShaderProgram(ShaderProgramKeys.POSITION_COLOR))
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
                    .build(false)
            )
    }

    val mouse = MinecraftClient.getInstance().mouse
    val window = MinecraftClient.getInstance().window
    override fun disableDepth() {
        RenderSystem.disableDepthTest()
    }

    override fun enableDepth() {
        RenderSystem.enableDepthTest()
    }

    fun NativeImageBackedTexture.setData(img: BufferedImage) {
        for (i in (0 until img.width)) {
            for (j in (0 until img.height)) {
                val argb = img.getRGB(i, j)
                image!!.setColorArgb(i, j, argb)
            }
        }
    }

    override fun generateDynamicTexture(img: BufferedImage): DynamicTextureReference {
        val texture = NativeImageBackedTexture(img.width, img.height, true)
        texture.setData(img)
        texture.upload()
        val res = MinecraftClient.getInstance().textureManager
            .registerDynamicTexture("moulconfig", texture)
        return object : DynamicTextureReference() {
            override fun update(bufferedImage: BufferedImage) {
                texture.setData(img)
                texture.upload()
            }

            override val identifier: MyResourceLocation
                get() = ModernMinecraft.fromIdentifier(res)

            override fun doDestroy() {
                MinecraftClient.getInstance().textureManager.destroyTexture(res)
            }
        }
    }


    override fun refreshScissor() {
        drawContext.setScissor(drawContext.scissorStack.stack.peekLast())
    }

    override fun disableScissor() {
        drawContext.setScissor(null)
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

    var tintR = 1F
    var tintG = 1F
    var tintB = 1F
    var tintA = 1F

    override fun color(r: Float, g: Float, b: Float, a: Float) {
        tintR = r
        tintG = g
        tintB = b
        tintA = a
    }

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return GLFW.glfwGetMouseButton(window.handle, mouseButton) == GLFW.GLFW_PRESS
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return InputUtil.isKeyPressed(window.handle, keyboardKey)
    }


    override fun drawTriangles(vararg coordinates: Float) {
        require(coordinates.size % 6 == 0)
        RenderSystem.enableBlend()
        drawContext.draw {
            val buf = it.getBuffer(COLORED_TRIANGLES)
            val matrix = drawContext.matrices.peek().positionMatrix

            for (i in 0 until (coordinates.size / 2)) {
                buf.vertex(matrix, coordinates[i * 2], coordinates[i * 2 + 1], 0.0F)
                    .color(tintA, tintR, tintG, tintB).next()
            }
        }
        RenderSystem.disableBlend()
    }

    override fun drawString(
        fontRenderer: IFontRenderer,
        text: String,
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
        drawContext.draw {
            val matrix = drawContext.matrices.peek().positionMatrix
            val buffer = it.getBuffer(RL)
            buffer.vertex(matrix, left, bottom, 0F).next()
            buffer.vertex(matrix, right, bottom, 0F).next()
            buffer.vertex(matrix, right, top, 0F).next()
            buffer.vertex(matrix, left, top, 0F).next()
        }
    }

    override fun setTextureMinMagFilter(textureFilter: TextureFilter) {
        // TODO bind texture first
        val filter = when (textureFilter) {
            TextureFilter.LINEAR -> GL11.GL_LINEAR
            TextureFilter.NEAREST -> GL11.GL_NEAREST
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter)
    }

    override fun drawTexturedRect(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        u1: Float,
        v1: Float,
        u2: Float,
        v2: Float
    ) {
        drawContext.draw {
            val matrix4f: Matrix4f = drawContext.matrices.peek().positionMatrix
            val bufferBuilder = it.getBuffer(RenderLayer.getGuiTextured(ModernMinecraft.boundTexture!!))
            bufferBuilder.vertex(matrix4f, x, y, 0F).texture(u1, v1)
                .color(tintA, tintG, tintB, tintA).next()
            bufferBuilder.vertex(matrix4f, x, y + height, 0f).texture(u1, v2)
                .color(tintA, tintG, tintB, tintA).next()
            bufferBuilder.vertex(matrix4f, x + width, y + height, 0f).texture(u2, v2)
                .color(tintA, tintG, tintB, tintA).next()
            bufferBuilder.vertex(matrix4f, x + width, y, 0F).texture(u2, v1)
                .color(tintA, tintG, tintB, tintA).next()
        }
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
        drawContext.enableScissor(left, top, right, bottom)
    }

    override fun popScissor() {
        drawContext.disableScissor()
    }

    override fun clearScissor() {
        drawContext.scissorStack.stack.clear();
        drawContext.setScissor(null)
    }

    override fun renderItemStack(itemStack: IItemStack, x: Int, y: Int, overlayText: String?) {
        val item = (itemStack as ModernItemStack).backing
        drawContext.drawItem(item, x, y)
        drawContext.drawStackOverlay(
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
