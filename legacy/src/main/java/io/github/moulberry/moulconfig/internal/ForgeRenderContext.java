package io.github.moulberry.moulconfig.internal;

import io.github.moulberry.moulconfig.common.IItemStack;
import io.github.moulberry.moulconfig.common.RenderContext;
import io.github.moulberry.moulconfig.forge.ForgeItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

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
    public void translate(float x, float y, float z) {
        GlStateManager.translate(x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        GlStateManager.scale(x, y, z);
    }

    @Override
    public void color(float r, float g, float b, float a) {
        GlStateManager.color(r, g, b, a);
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
    public void drawColoredRect(float left, float top, float right, float bottom, int color) {
        RenderUtils.drawGradientRect(0, (int) left, (int) top, (int) right, (int) bottom, color, color);
    }

    @Override
    public void invertedRect(float left, float top, float right, float bottom) {
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GL11.GL_OR_REVERSE);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(right, top, 0).endVertex();
        worldrenderer.pos(left, top, 0).endVertex();
        worldrenderer.pos(left, bottom, 0).endVertex();
        worldrenderer.pos(right, bottom, 0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableColorLogic();
    }

    @Override
    public void drawTexturedRect(float x, float y, float width, float height) {
        RenderUtils.drawTexturedRect(x, y, width, height, GL11.GL_NEAREST);
    }

    @Override
    public void renderDarkRect(int x, int y, int width, int height) {
        RenderUtils.drawFloatingRectDark(x, y, width, height);
    }

    @Override
    public void pushScissor(int left, int top, int right, int bottom) {
        GlScissorStack.push(left, top, right, bottom, new ScaledResolution(Minecraft.getMinecraft()));
    }

    @Override
    public void popScissor() {
        GlScissorStack.pop(new ScaledResolution(Minecraft.getMinecraft()));
    }

    @Override
    public void renderItemStack(@NotNull IItemStack itemStack, int x, int y, @Nullable String overlayText) {
        ForgeItemStack forgeStack = (ForgeItemStack) itemStack;
        ItemStack backing = forgeStack.getBacking();
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemAndEffectIntoGUI(backing, x, y);
        if (overlayText != null)
            renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, backing, x, y, overlayText);
        RenderHelper.disableStandardItemLighting();
    }

}
