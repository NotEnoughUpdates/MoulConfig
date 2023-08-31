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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

public class TextRenderUtils {
    public static int getCharVertLen(char c) {
        if ("acegmnopqrsuvwxyz".indexOf(c) >= 0) {
            return 5;
        } else {
            return 7;
        }
    }

    @SuppressWarnings("unused")
    public static float getVerticalHeight(String str) {
        str = StringUtils.cleanColour(str);
        float height = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int charHeight = getCharVertLen(c);
            height += charHeight + 1.5f;
        }
        return height;
    }

    public static String getFormat(String text) {
        text = text.toLowerCase();
        StringBuilder s = new StringBuilder();
        int index = -1;
        int length = text.length();

        while ((index = text.indexOf(167, index + 1)) != -1) {
            if (index < length - 1) {
                char ch = text.charAt(index + 1);
                if (isFormatColor(ch)) {
                    s = new StringBuilder("§" + ch);
                } else if (isFormatSpecial(ch)) {
                    s.append("§").append(ch);
                }
            }
        }

        return s.toString();
    }

    private static boolean isFormatColor(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f');
    }

    private static boolean isFormatSpecial(char ch) {
        return (ch >= 'k' && ch <= 'o') || ch == 'r';
    }

    @SuppressWarnings("unused")
    public static void drawStringVertical(String str, DrawContext fr, float x, float y, boolean shadow, int colour) {
        String format = getFormat(str);
        str = StringUtils.cleanColour(str);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            int charHeight = getCharVertLen(c);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int charWidth = textRenderer.getWidth(String.valueOf(c));
            fr.drawText(textRenderer, format + c, (int) (x + (5 - charWidth) / 2f), (int) (y - 7 + charHeight), colour, shadow);

            y += charHeight + 1.5f;
        }
    }

    public static void drawStringScaledMaxWidth(
            String str,
            DrawContext fr,
            float x,
            float y,
            boolean shadow,
            int len,
            int colour
    ) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int strLen = textRenderer.getWidth(str);
        float factor = len / (float) strLen;
        factor = Math.min(1, factor);

        drawStringScaled(str, fr, x, y, shadow, colour, factor);
    }

    @SuppressWarnings("unused")
    public static void drawStringCentered(String str, DrawContext fr, float x, float y, boolean shadow, int colour) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int strLen = textRenderer.getWidth(str);

        float x2 = x - strLen / 2f;
        float y2 = y - textRenderer.fontHeight / 2f;

        fr.getMatrices().push();
        fr.getMatrices().translate(x2, y2, 0);
        fr.drawText(textRenderer, str, 0, 0, colour, shadow);
        fr.getMatrices().pop();
    }

    public static void drawStringScaled(
            String str,
            DrawContext fr,
            float x,
            float y,
            boolean shadow,
            int colour,
            float factor
    ) {
        drawStringScaled(Text.literal(str), fr, x, y, shadow, colour, factor);
    }

    public static void drawStringScaled(
            Text str,
            DrawContext fr,
            float x,
            float y,
            boolean shadow,
            int colour,
            float factor
    ) {
        fr.getMatrices().scale(factor, factor, 1);
        fr.drawText(MinecraftClient.getInstance().textRenderer, str, (int) (x / factor), (int) (y / factor), colour, shadow);
        fr.getMatrices().scale(1 / factor, 1 / factor, 1);
    }

    public static void drawStringCenteredScaledMaxWidth(
            String str,
            DrawContext fr,
            float x,
            float y,
            boolean shadow,
            int len,
            int colour
    ) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int strLen = textRenderer.getWidth(str);
        float factor = len / (float) strLen;
        factor = Math.min(1, factor);
        int newLen = Math.min(strLen, len);

        float fontHeight = 8 * factor;

        drawStringScaled(str, fr, x - newLen / 2.0f, y - fontHeight / 2, shadow, colour, factor);
    }

    public static void drawStringCenteredScaledMaxWidth(
            Text str,
            DrawContext fr,
            float x,
            float y,
            boolean shadow,
            int len,
            int colour
    ) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int strLen = textRenderer.getWidth(str);
        float factor = len / (float) strLen;
        factor = Math.min(1, factor);
        int newLen = Math.min(strLen, len);

        float fontHeight = 8 * factor;

        drawStringScaled(str, fr, x - newLen / 2.0f, y - fontHeight / 2, shadow, colour, factor);
    }

    /*
    public static void renderToolTip(
            ItemStack stack,
            int mouseX,
            int mouseY,
            int screenWidth,
            int screenHeight,
            DrawContext drawContext
    ) {
        List<Text> list = stack.getTooltip(
                MinecraftClient.getInstance().player,
                MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.ADVANCED : TooltipContext.BASIC
        );

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, ((MutableText)list.get(i)).formatted(stack.getRarity().formatting));
            } else {
                list.set(i, ((MutableText) list.get(i)).formatted(Formatting.GRAY));
            }
        }

        drawHoveringText(list, mouseX, mouseY, screenWidth, screenHeight, -1);
    }

    public static void drawHoveringText(
            List<Text> textLines,
            final int mouseX,
            final int mouseY,
            final int screenWidth,
            final int screenHeight,
            final int maxTextWidth,
            DrawContext drawContext
    ) {
        if (!textLines.isEmpty()) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (Text textLine : textLines) {
                int textLineWidth = textRenderer.getWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2) {
                        tooltipTextWidth = mouseX - 12 - 8;
                    } else {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<>();
                for (int i = 0; i < textLines.size(); i++) {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine) {
                        int lineWidth = font.getWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2) {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                } else {
                    tooltipX = mouseX + 12;
                }
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY + tooltipHeight + 6 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 6;
            }

            final int zLevel = 300;
            final int backgroundColor = 0xF0100010;
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY - 4,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 3,
                    backgroundColor,
                    backgroundColor
            );
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY + tooltipHeight + 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipHeight + 4,
                    backgroundColor,
                    backgroundColor
            );
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipHeight + 3,
                    backgroundColor,
                    backgroundColor
            );
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX - 4,
                    tooltipY - 3,
                    tooltipX - 3,
                    tooltipY + tooltipHeight + 3,
                    backgroundColor,
                    backgroundColor
            );
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 4,
                    tooltipY + tooltipHeight + 3,
                    backgroundColor,
                    backgroundColor
            );
            final int borderColorStart = 0x505000FF;
            final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY - 3 + 1,
                    tooltipX - 3 + 1,
                    tooltipY + tooltipHeight + 3 - 1,
                    borderColorStart,
                    borderColorEnd
            );
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX + tooltipTextWidth + 2,
                    tooltipY - 3 + 1,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipHeight + 3 - 1,
                    borderColorStart,
                    borderColorEnd
            );
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 3 + 1,
                    borderColorStart,
                    borderColorStart
            );
            RenderUtils.drawGradientRect(
                    zLevel,
                    tooltipX - 3,
                    tooltipY + tooltipHeight + 2,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipHeight + 3,
                    borderColorEnd,
                    borderColorEnd
            );

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
        GlStateManager.disableLighting();
    }

    */
}
