package io.github.notenoughupdates.moulconfig.platform

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.platform.LogicOp
import com.mojang.blaze3d.vertex.VertexFormat
import io.github.notenoughupdates.moulconfig.common.*
import io.github.notenoughupdates.moulconfig.internal.FilterAssertionCache
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer

class ModernRenderContext(val drawContext: DrawContext) : RenderContext {
    companion object {
        val INVERTED_RECT_PIPE = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withColorLogic(LogicOp.OR_REVERSE)
            .withLocation("moulconfig_inverted_rect")
            .build()
        val INVERTED_RECT = RenderLayer.MultiPhase.of(
            "moulconfig_inverted_rect",
            RenderLayer.DEFAULT_BUFFER_SIZE,
            INVERTED_RECT_PIPE,
            RenderLayer.MultiPhaseParameters.builder()
                .build(false)
        )
        val COLORED_TRIANGLES_PIPE =
            RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
                .withLocation("moulconfig_colored_triangles")
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
                .build()
        val COLORED_TRIANGLES =
            RenderLayer.MultiPhase.of(
                "moulconfig_colored_triangles",
                RenderLayer.DEFAULT_BUFFER_SIZE,
                COLORED_TRIANGLES_PIPE,
                RenderLayer.MultiPhaseParameters.builder()

                    .build(false)
            )
    }

    val mouse = MinecraftClient.getInstance().mouse
    val window = MinecraftClient.getInstance().window

    override fun pushMatrix() {
        drawContext.matrices.push()
    }

    override fun popMatrix() {
        drawContext.matrices.pop()
    }

    override fun translate(x: Float, y: Float) {
        drawContext.matrices.translate(x, y, 0F)
    }

    override fun scale(x: Float, y: Float) {
        drawContext.matrices.scale(x, y, 1F)
    }

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return GLFW.glfwGetMouseButton(window.handle, mouseButton) == GLFW.GLFW_PRESS
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return InputUtil.isKeyPressed(window.handle, keyboardKey)
    }


    override fun drawColoredTriangles(color: Int, vararg coordinates: Float) {
        require(coordinates.size % 6 == 0)
        drawContext.draw {
            val buf = it.getBuffer(COLORED_TRIANGLES)
            val matrix = drawContext.matrices.peek().positionMatrix

            for (i in 0 until (coordinates.size / 2)) {
                buf.vertex(matrix, coordinates[i * 2], coordinates[i * 2 + 1], 0.0F)
                    .color(color).next()
            }
        }
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

    override fun invertedRect(left: Float, top: Float, right: Float, bottom: Float, additiveColor: Int) {
        drawContext.draw {
            val matrix = drawContext.matrices.peek().positionMatrix
            val buffer = it.getBuffer(INVERTED_RECT)
            buffer.vertex(matrix, left, bottom, 0F).color(additiveColor).next()
            buffer.vertex(matrix, right, bottom, 0F).color(additiveColor).next()
            buffer.vertex(matrix, right, top, 0F).color(additiveColor).next()
            buffer.vertex(matrix, left, top, 0F).color(additiveColor).next()
        }
    }

    override fun drawTexturedTintedRect(
        texture: MyResourceLocation,
        x: Float, y: Float,
        width: Float, height: Float,
        u1: Float, v1: Float, u2: Float, v2: Float,
        color: Int, filter: TextureFilter,
    ) { // TODO: transform this into a class
        FilterAssertionCache.assertTextureFilter(texture, filter)
        drawContext.draw {
            MinecraftClient.getInstance()
                .textureManager
                .getTexture(MoulConfigPlatform.fromMyResourceLocation(texture))
                .setFilter(
                    when (filter) {
                        TextureFilter.LINEAR -> true
                        TextureFilter.NEAREST -> false
                    },
                    false
                )
            val matrix4f: Matrix4f = drawContext.matrices.peek().positionMatrix
            val bufferBuilder = it.getBuffer(RenderLayer.getGuiTextured(MoulConfigPlatform.fromMyResourceLocation(texture)))
            bufferBuilder.vertex(matrix4f, x, y, 0F).texture(u1, v1)
                .color(color).next()
            bufferBuilder.vertex(matrix4f, x, y + height, 0f).texture(u1, v2)
                .color(color).next()
            bufferBuilder.vertex(matrix4f, x + width, y + height, 0f).texture(u2, v2)
                .color(color).next()
            bufferBuilder.vertex(matrix4f, x + width, y, 0F).texture(u2, v1)
                .color(color).next()
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
        // Do not use drawContext.enableScissor() since that transform coords
        // In order to be compatible with 1.8.9, this method does not do that.
        drawContext.scissorStack.push(ScreenRect(left, top, right - left, bottom - top))
        refreshScissor()
    }

    private fun refreshScissor() {
        drawContext.setScissor(drawContext.scissorStack.stack.peek())
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

    override fun drawTooltipNow(x: Int, y: Int, tooltipLines: List<String?>) {
        drawContext.drawTooltip(
            MinecraftClient.getInstance().textRenderer,
            tooltipLines.map { Text.literal(it) },
            // TODO: we should improve render context somewhat
            //       and yet you participate in it.
            //       i am very smart
            x,
            y,
        )
    }

    override fun renderExtraLayers() {
        // Left blank: [drawOnTop] renders directly.
    }

    override fun drawOnTop(layer: Layer, scissorBehaviour: RenderContext.ScissorBehaviour, later: Consumer<RenderContext>) {
        pushMatrix()
        if (scissorBehaviour == RenderContext.ScissorBehaviour.ESCAPE) {
            pushScissor(0, 0, minecraft.scaledWidth, minecraft.scaledHeight)
        }
        drawContext.matrices.translate(0F, 0F, layer.sortIndex * 200F)
        later.accept(this)
        if (scissorBehaviour == RenderContext.ScissorBehaviour.ESCAPE) {
            popScissor()
        }
        popMatrix()
    }
}
