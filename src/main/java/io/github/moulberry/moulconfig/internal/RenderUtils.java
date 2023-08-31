/*
 * Copyright (C) 2023 NotEnoughUpdates contributors
 *
 * This file is part of MoulConfig.
 *
 * MoulConfig is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * MoulConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MoulConfig. If not, see <https://www.gnu.org/licenses/>.
 *
 */

/**/
package io.github.moulberry.moulconfig.internal;

import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class RenderUtils {
    public static void drawFloatingRectDark(DrawContext context, int x, int y, int width, int height) {
        drawFloatingRectDark(context, x, y, width, height, true);
    }

    public static void drawFloatingRectDark(DrawContext context, int x, int y, int width, int height, boolean shadow) {
        int alpha = 0xff000000;

        int main = alpha | 0x202026;
        int light = 0xff303036;
        int dark = 0xff101016;
        context.fill(x, y, x + 1, y + height, light); //Left
        context.fill(x + 1, y, x + width, y + 1, light); //Top
        context.fill(x + width - 1, y + 1, x + width, y + height, dark); //Right
        context.fill(x + 1, y + height - 1, x + width - 1, y + height, dark); //Bottom
        context.fill(x + 1, y + 1, x + width - 1, y + height - 1, main); //Middle
        if (shadow) {
            context.fill(x + width, y + 2, x + width + 2, y + height + 2, 0x70000000); //Right shadow
            context.fill(x + 2, y + height, x + width, y + height + 2, 0x70000000); //Bottom shadow
        }
    }

    public static void drawFloatingRect(DrawContext context, int x, int y, int width, int height) {
        drawFloatingRectWithAlpha(context, x, y, width, height, 0xFF, true);
    }

    public static void drawFloatingRectWithAlpha(DrawContext context, int x, int y, int width, int height, int alpha, boolean shadow) {
        int main = (alpha << 24) | 0xc0c0c0;
        int light = (alpha << 24) | 0xf0f0f0;
        int dark = (alpha << 24) | 0x909090;
        context.fill(x, y, x + 1, y + height, light); //Left
        context.fill(x + 1, y, x + width, y + 1, light); //Top
        context.fill(x + width - 1, y + 1, x + width, y + height, dark); //Right
        context.fill(x + 1, y + height - 1, x + width - 1, y + height, dark); //Bottom
        context.fill(x + 1, y + 1, x + width - 1, y + height - 1, main); //Middle
        if (shadow) {
            context.fill(x + width, y + 2, x + width + 2, y + height + 2, (alpha * 3 / 5) << 24); //Right shadow
            context.fill(x + 2, y + height, x + width, y + height + 2, (alpha * 3 / 5) << 24); //Bottom shadow
        }
    }

    public static void drawTexturedRect(DrawContext context, float x, float y, float width, float height) {
        drawTexturedRect(context, x, y, width, height, 0, 1, 0, 1);
    }

    public static void drawTexturedRect(DrawContext context, float x, float y, float width, float height, int filter) {
        drawTexturedRect(context, x, y, width, height, 0, 1, 0, 1, filter);
    }

    public static void drawTexturedRect(
            DrawContext drawContext,
            float x,
            float y,
            float width,
            float height,
            float uMin,
            float uMax,
            float vMin,
            float vMax
    ) {
        drawTexturedRect(drawContext, x, y, width, height, uMin, uMax, vMin, vMax, GL11.GL_NEAREST);
    }

    public static void drawTexturedRect(
            DrawContext context,
            float x,
            float y,
            float width,
            float height,
            float uMin,
            float uMax,
            float vMin,
            float vMax,
            int filter
    ) {
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawTexturedRectNoBlend(context, x, y, width, height, uMin, uMax, vMin, vMax, filter);
    }

    public static void drawTexturedRectNoBlend(
            DrawContext context,
            float x,
            float y,
            float width,
            float height,
            float uMin,
            float uMax,
            float vMin,
            float vMax,
            int filter
    ) {
//
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
//        Tessellator tessellator = Tessellator.getInstance();
//
//        WorldRenderer worldrenderer = MinecraftClient.getInstance().worldRenderer;
//        BufferBuilder bufferBuilder = new BufferBuilder(10);
//        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_TEXTURE);
        //context.fill(x, y, x+width, y+height, );
        //bufferBuilder
        //        .pos(x, y + height, 0.0D)
        //        .tex(uMin, vMax).endVertex();
        //bufferBuilder
        //        .pos(x + width, y + height, 0.0D)
        //        .tex(uMax, vMax).endVertex();
        //bufferBuilder
        //        .pos(x + width, y, 0.0D)
        //        .tex(uMax, vMin).endVertex();
        //bufferBuilder
        //        .pos(x, y, 0.0D)
        //        .tex(uMin, vMin).endVertex();
        //tessellator.draw();
//
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    public static void drawGradientRect(
            DrawContext context,
            int left,
            int top,
            int right,
            int bottom,
            int startColor,
            int endColor
    ) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        context.fillGradient(left, top, right, bottom, startColor, endColor);

//        GlStateManager.disableTexture2D();
//        GlStateManager.enableBlend();
//        GlStateManager.disableAlpha();
//        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//        GlStateManager.shadeModel(7425);
//
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_ALPHA);
//
//
//        Tessellator tessellator = Tessellator.getInstance();
//        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
//        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
//        worldrenderer.pos(right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
//        worldrenderer.pos(left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
//        worldrenderer.pos(left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
//        worldrenderer.pos(right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
//        tessellator.draw();
//
//        GlStateManager.shadeModel(7424);
//        GlStateManager.disableBlend();
//        GlStateManager.enableAlpha();
//        GlStateManager.enableTexture2D();
    }

}
