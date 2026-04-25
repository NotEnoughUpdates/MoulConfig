package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IItemStack;
import io.github.notenoughupdates.moulconfig.common.Layer;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.common.TextureFilter;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.forge.ForgeItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ForgeRenderContext implements RenderContext {
    @Override
    public void pushMatrix() {
        GlStateManager.pushMatrix();
    }

    @Override
    public void popMatrix() {
        GlStateManager.popMatrix();
    }

    @Override
    public void translate(float x, float y) {
        GlStateManager.translate(x, y, 0F);
    }

    @Override
    public void scale(float x, float y) {
        GlStateManager.scale(x, y, 1F);
    }

    @Override
    public boolean isMouseButtonDown(int mouseButton) {
        return Mouse.isButtonDown(mouseButton);
    }

    @Override
    public boolean isKeyboardKeyDown(int keyboardKey) {
        return Keyboard.isKeyDown(keyboardKey);
    }

    @Override
    public void drawString(IFontRenderer fontRenderer, StructuredText text, int x, int y, int color, boolean shadow) {
        ((ForgeFontRenderer) fontRenderer).font.drawString(StructuredTextImpl.unwrap(text).getFormattedText(), (float) x, (float) y, color, shadow);
    }

    @Override
    public void drawColoredRect(float left, float top, float right, float bottom, int color) {
        RenderUtils.drawGradientRect(0, (int) left, (int) top, (int) right, (int) bottom, color, color);
    }

    public void applyGlobalColor(int color) {
        GlStateManager.color(
            ColourUtil.unpackARGBRedF(color),
            ColourUtil.unpackARGBGreenF(color),
            ColourUtil.unpackARGBBlueF(color),
            ColourUtil.unpackARGBAlphaF(color)
        );
    }

    @Override
    public void drawColouredQuads(int colour, float... coordinates) {
        Tessellator tessellator = Tessellator.getInstance();
        net.minecraft.client.renderer.WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        applyGlobalColor(colour);
        if (coordinates.length % 8 != 0) {
            throw new IllegalArgumentException("Quad coordinates must be groups of four points");
        }
        for (int i = 0; i < coordinates.length / 2; i++) {
            worldrenderer.pos(coordinates[i * 2], coordinates[i * 2 + 1], 0.0).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @Override
    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        RenderUtils.drawGradientRect(0, left, top, right, bottom, startColor, endColor);
    }

    @Override
    public void invertedRect(float left, float top, float right, float bottom, int additiveColor) {
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GL11.GL_OR_REVERSE);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        net.minecraft.client.renderer.WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        applyGlobalColor(additiveColor);
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableColorLogic();
    }

    @Override
    public void drawTexturedTintedRect(MyResourceLocation texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2, int color, TextureFilter filter) {
        FilterAssertionCache.assertTextureFilter(texture, filter);
        applyGlobalColor(color);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ForgeMinecraft.fromMyResourceLocation(texture));
        RenderUtils.drawTexturedRect(
            x, y, width, height, u1, u2, v1, v2,
            filter == TextureFilter.LINEAR ? GL11.GL_LINEAR : GL11.GL_NEAREST
        );
    }

    @Override
    public void drawDarkRect(int x, int y, int width, int height, boolean shadow) {
        RenderUtils.drawFloatingRectDark(x, y, width, height, shadow);
    }

    @Override
    public void pushScissor(int left, int top, int right, int bottom) {
        GlScissorStack.push(left, top, right, bottom, new ScaledResolution(Minecraft.getMinecraft()), false);
    }

    @Override
    public void pushRawScissor(int left, int top, int right, int bottom) {
        GlScissorStack.push(left, top, right, bottom, new ScaledResolution(Minecraft.getMinecraft()), true);
    }

    @Override
    public void popScissor() {
        GlScissorStack.pop(new ScaledResolution(Minecraft.getMinecraft()));
    }

    @Override
    public void assertNoScissors() {
        if (!GlScissorStack.isEmpty()) {
            Warnings.warn("no scissor assertion failed", 4);
        }
    }

    @Override
    public void clearScissor() {
        GlScissorStack.clear();
    }

    @Override
    public void renderItemStack(IItemStack itemStack, int x, int y, StructuredText overlayText) {
        ForgeItemStack forgeStack = (ForgeItemStack) itemStack;
        net.minecraft.item.ItemStack backing = forgeStack.getBacking();
        net.minecraft.client.renderer.entity.RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemAndEffectIntoGUI(backing, x, y);
        if (overlayText != null) {
            renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, backing, x, y, overlayText.getText());
        }
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public void drawTooltipNow(int x, int y, List<StructuredText> tooltipLines) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
        int mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth;
        int mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1;
        List<IChatComponent> components = new ArrayList<>();
        for (StructuredText line : tooltipLines) {
            components.add(StructuredTextImpl.unwrap(line));
        }
        TextRenderUtils.drawHoveringText(components, mouseX, mouseY, width, height, -1, Minecraft.getMinecraft().fontRendererObj);
    }

    @Override
    public void drawOnTop(Layer layer, ScissorBehaviour scissorBehaviour, Consumer<RenderContext> later) {
        pushMatrix();
        if (scissorBehaviour == ScissorBehaviour.ESCAPE) {
            pushRawScissor(0, 0, getMinecraft().getScaledWidth(), getMinecraft().getScaledHeight());
        }
        GlStateManager.translate(0F, 0F, (float) layer.getSortIndex());
        later.accept(this);
        if (scissorBehaviour == ScissorBehaviour.ESCAPE) {
            popScissor();
        }
        popMatrix();
    }

    @Override
    public void renderExtraLayers() {
    }
}
