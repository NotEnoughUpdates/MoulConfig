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

package io.github.moulberry.moulconfig.gui;

import io.github.moulberry.moulconfig.internal.RenderUtils;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import io.github.moulberry.moulconfig.processor.ProcessedOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.StringVisitable;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

public abstract class GuiOptionEditor {
    private static final int HEIGHT = 45;
    protected final ProcessedOption option;
    private String searchDescNameCache;

    @ApiStatus.Internal
    public ProcessedOption getOption() {
        return option;
    }

    public GuiOptionEditor(ProcessedOption option) {
        this.option = option;
    }

    public void render(DrawContext context, int x, int y, int width) {
        int height = getHeight();

        RenderUtils.drawFloatingRectDark(context, x, y, width, height, true);
        TextRenderUtils.drawStringCenteredScaledMaxWidth(option.name,
                context, x + width / 6, y + 13, true, width / 3 - 10, 0xc0c0c0
        );

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int maxLines = 5;
        float scale = 1;
        int lineCount = textRenderer.wrapLines(StringVisitable.plain(option.desc), width * 2 / 3 - 10).size();

        if (lineCount == 0) return;

        float paraHeight = 9 * lineCount - 1;

        while (paraHeight >= HEIGHT - 10) {
            scale -= 1 / 8f;
            lineCount = textRenderer.wrapLines(StringVisitable.plain(option.desc), (int) (width * 2 / 3 / scale - 10)).size();
            paraHeight = (int) (9 * scale * lineCount - 1 * scale);
        }

        context.getMatrices().push();
        context.getMatrices().translate(x + 5 + width / 3f, y + HEIGHT / 2f - paraHeight / 2, 0);
        context.getMatrices().scale(scale, scale, 1);

        context.drawTextWrapped(textRenderer, StringVisitable.plain(option.desc), 0, 0, (int) (width * 2 / 3 / scale - 10), 0xc0c0c0);

        context.getMatrices().pop();
    }

    public int getHeight() {
        return HEIGHT;
    }

    public abstract boolean mouseInput(int x,
                                       int y,
                                       int width,
                                       double mouseX,
                                       double mouseY,
                                       int button);

    public abstract boolean keyboardInput(int keyCode,
                                          int scanCode,
                                          int modifiers);

    public boolean mouseInputOverlay(int x,
                                     int y,
                                     int width,
                                     double mouseX,
                                     double mouseY,
                                     int button) {
        return false;
    }

    public void renderOverlay(DrawContext context,
                              int x,
                              int y,
                              int width) {
    }

    public boolean fulfillsSearch(String word) {
        if (searchDescNameCache == null) {
            searchDescNameCache = (option.name + option.desc + option.hiddenKeys).toLowerCase(Locale.ROOT);
        }
        return searchDescNameCache.contains(word);
    }

    public boolean mouseDragged(int finalX,
                                int finalY,
                                int finalWidth,
                                double mouseX,
                                double mouseY,
                                int button,
                                double deltaX,
                                double deltaY) {
        return false;
    }

    public boolean mouseReleased(int finalX,
                                 int finalY,
                                 int finalWidth,
                                 double mouseX,
                                 double mouseY,
                                 int button) {
        return false;
    }
}
