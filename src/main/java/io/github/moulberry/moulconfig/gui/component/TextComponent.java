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

package io.github.moulberry.moulconfig.gui.component;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.gui.GuiImmediateContext;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import lombok.RequiredArgsConstructor;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * A gui element which renders a string in a single line
 */
@RequiredArgsConstructor
public class TextComponent extends GuiComponent {
    final FontRenderer fontRenderer;
    final Supplier<String> string;
    final int width;
    final TextAlignment alignment;
    final boolean shadow;
    final boolean split;
    private String lastString;
    private List<String> lastSplit;
    private static final Pattern colorPattern = Pattern.compile("§[a-f0-9r]");


    public TextComponent(String string, int width) {
        this(Minecraft.getMinecraft().fontRendererObj, () -> string, width, TextAlignment.LEFT, false, false);
    }

    public TextComponent(String string) {
        this(string, Minecraft.getMinecraft().fontRendererObj.getStringWidth(string));
    }

    @Override
    public int getWidth() {
        return width + 4;
    }

    @Override
    public int getHeight() {
        return 2 + (fontRenderer.FONT_HEIGHT + 2) * split(string.get()).size();
    }

    public List<String> split(String text) {
        if (!split) return Arrays.asList(text);
        if (Objects.equals(text, lastString))
            return lastSplit;
        lastString = text;
        List<IChatComponent> iChatComponents = GuiUtilRenderComponents.splitText(new ChatComponentText(text), width, fontRenderer, false, false);
        String lastFormat = "§r";
        List<String> strings = new ArrayList<>(iChatComponents.size());
        for (IChatComponent iChatComponent : iChatComponents) {
            String formattedText = lastFormat + iChatComponent.getFormattedText().replaceAll("^((§.)*) *", "$1");
            strings.add(formattedText);
            var matcher = colorPattern.matcher(formattedText);
            while (matcher.find()) {
                lastFormat = matcher.group(0);
            }
        }
        return lastSplit = strings;
    }

    @Override
    public void render(GuiImmediateContext context) {
        GlStateManager.pushMatrix();
        List<String> lines = split(string.get());
        for (String line : lines) {
            int length = fontRenderer.getStringWidth(line);
            if (length > width) {
                TextRenderUtils.drawStringScaledMaxWidth(line, fontRenderer, 2, 2, shadow, width, -1);
            }
            switch (alignment) {
                case LEFT:
                    fontRenderer.drawString(line, 2, 2, -1, shadow);
                    break;
                case CENTER:
                    fontRenderer.drawString(line, width / 2 - length / 2 + 2, 2, -1, shadow);
                    break;
                case RIGHT:
                    fontRenderer.drawString(line, width - length + 2, 2, -1, shadow);
                    break;
            }
            GlStateManager.translate(0, fontRenderer.FONT_HEIGHT + 2, 0);
        }
        GlStateManager.popMatrix();
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT;
    }
}
