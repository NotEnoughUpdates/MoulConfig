package io.github.notenoughupdates.moulconfig.platform

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.vertex.VertexFormat
import io.github.notenoughupdates.moulconfig.common.*
import io.github.notenoughupdates.moulconfig.internal.FilterAssertionCache
import io.github.notenoughupdates.moulconfig.internal.Warnings
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.joml.Matrix3x2f
import org.lwjgl.glfw.GLFW
import java.util.*
import java.util.function.Consumer

class ModernRenderContext(val drawContext: DrawContext) : RenderContext {
    companion object {
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

    val window = MinecraftClient.getInstance().window

    override fun pushMatrix() { // TODO: cope with the massively reduced stack capacity
        drawContext.matrices.pushMatrix()
    }

    override fun popMatrix() {
        drawContext.matrices.popMatrix()
    }

    override fun translate(x: Float, y: Float) {
        drawContext.matrices.translate(x, y)
    }

    override fun scale(x: Float, y: Float) {
        drawContext.matrices.scale(x, y)
    }

    override fun isMouseButtonDown(mouseButton: Int): Boolean {
        return GLFW.glfwGetMouseButton(window.handle, mouseButton) == GLFW.GLFW_PRESS
    }

    override fun isKeyboardKeyDown(keyboardKey: Int): Boolean {
        return InputUtil.isKeyPressed(window.handle, keyboardKey)
    }


    override fun drawColoredTriangles(color: Int, vararg coordinates: Float) {
        require(coordinates.size % 6 == 0)
//        drawContext.draw { // TODO
//            val buf = it.getBuffer(COLORED_TRIANGLES)
//            val matrix = drawContext.matrices.peek().positionMatrix
//
//            for (i in 0 until (coordinates.size / 2)) {
//                buf.vertex(matrix, coordinates[i * 2], coordinates[i * 2 + 1], 0.0F)
//                    .color(color).next()
//            }
//        }
    }

    override fun drawString(
        fontRenderer: IFontRenderer,
        text: String,
        x: Int,
        y: Int,
        color: Int,
        shadow: Boolean
    ) {
        drawContext.drawText((fontRenderer as ModernFontRenderer).textRenderer, text, x, y, color, shadow)
    }

    override fun drawColoredRect(left: Float, top: Float, right: Float, bottom: Float, color: Int) {
        drawContext.fill(left.toInt(), top.toInt(), right.toInt(), bottom.toInt(), color)
    }

    override fun invertedRect(left: Float, top: Float, right: Float, bottom: Float, additiveColor: Int) {
        drawContext.fill(RenderPipelines.GUI_INVERT, left, top, right, bottom, -1)
        drawContext.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, left, top, right, bottom, additiveColor)
    }

    override fun drawTexturedTintedRect(
        texture: MyResourceLocation,
        x: Float, y: Float,
        width: Float, height: Float,
        u1: Float, v1: Float, u2: Float, v2: Float,
        color: Int, filter: TextureFilter,
    ) {
        FilterAssertionCache.assertTextureFilter(texture, filter)
        val identifier = MoulConfigPlatform.fromMyResourceLocation(texture)
        MinecraftClient.getInstance()
            .textureManager
            .getTexture(identifier)
            .setFilter(
                when (filter) {
                    TextureFilter.LINEAR -> true
                    TextureFilter.NEAREST -> false
                },
                false
            )
        drawContext.drawTexturedQuad(
            RenderPipelines.GUI_TEXTURED,
            identifier,
            x.toInt(), y.toInt(), (x + width).toInt(), (y + width).toInt(),
            u1, u2, v1, v2,
            color
        )
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
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        startColor: Int,
        endColor: Int
    ) {
        drawContext.fillGradient(left, top, right, bottom, startColor, endColor)
    }

    override fun pushRawScissor(left: Int, top: Int, right: Int, bottom: Int) {
        drawContext.scissorStack.stack.addLast(ScreenRect(left, top, right - left, bottom - top))
    }

    override fun pushScissor(left: Int, top: Int, right: Int, bottom: Int) {
        drawContext.enableScissor(left, top, right, bottom)
    }

    override fun popScissor() {
        drawContext.disableScissor()
    }

    @Deprecated("Deprecated in Java")
    override fun clearScissor() {
        drawContext.scissorStack.stack.clear();
    }

    override fun assertNoScissors() {
        if (!drawContext.scissorStack.stack.isEmpty()) {
            Warnings.warn("Scissors found despite no scissor assertion", 4)
        }
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
        var currentLayer = Layer.ROOT
        while (true) {
            val nextLayer = queuedLayers.ceilingEntry(currentLayer) ?: break
            currentLayer = nextLayer.key
            val draws = nextLayer.value
            for (action in draws) {
                if (!drawContext.scissorStack.stack.isEmpty()) {
                    Warnings.warn("Scissors found despite no scissor assertion", 4)
                }
                action.scissorTop?.let { rect ->
                    pushRawScissor(rect.left, rect.top, rect.right, rect.bottom)
                }
                drawContext.matrices.pushMatrix()
                drawContext.matrices.set(action.transform)

                action.action.accept(this)

                drawContext.matrices.popMatrix()

                if (action.scissorTop != null)
                    popScissor()
                if (!drawContext.scissorStack.stack.isEmpty()) {
                    Warnings.warn("Scissors found despite no scissor assertion after execution of ${action.action}", 4)
                }
            }
        }
    }

    class DrawAction(
        val action: Consumer<RenderContext>,
        val scissorTop: ScreenRect?,
        val transform: Matrix3x2f,
    )

    val queuedLayers: NavigableMap<Layer, MutableList<DrawAction>> = TreeMap()

    override fun drawOnTop(layer: Layer, scissorBehaviour: RenderContext.ScissorBehaviour, later: Consumer<RenderContext>) {
        val layer = queuedLayers.getOrPut(layer) { mutableListOf() }
        layer.add(
            DrawAction(
                later,
                when (scissorBehaviour) {
                    RenderContext.ScissorBehaviour.ESCAPE -> null
                    RenderContext.ScissorBehaviour.INHERIT -> drawContext.scissorStack.peekLast()
                },
                Matrix3x2f(drawContext.matrices)
            )
        )
    }
}
