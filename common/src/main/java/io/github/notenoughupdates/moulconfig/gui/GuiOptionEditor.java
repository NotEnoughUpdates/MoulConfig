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

package io.github.notenoughupdates.moulconfig.gui;

import io.github.notenoughupdates.moulconfig.DescriptionRendereringBehaviour;
import io.github.notenoughupdates.moulconfig.annotations.SearchTag;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import lombok.var;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

public abstract class GuiOptionEditor {
    private static final int HEIGHT = 45;
    protected final ProcessedOption option;
    public MoulConfigEditor<?> activeConfigGUI;
    private String searchDescNameCache;
    private String searchTags = "";

    @ApiStatus.Internal
    public ProcessedOption getOption() {
        return option;
    }

    public GuiOptionEditor(ProcessedOption option) {
        this.option = option;
        for (SearchTag searchTag : option.getSearchTags()) {
            if (!searchTags.isEmpty()) {
                searchTags += " ";
            }
            searchTags += searchTag;
        }
    }

    public void render(RenderContext context, int x, int y, int width) {
        int height = getHeight();

        var minecraft = context.getMinecraft();
        var fr = minecraft.getDefaultFontRenderer();

        context.drawDarkRect(x, y, width, height, true);
        context.drawStringCenteredScaledMaxWidth(option.getName(),
            fr, x + width / 6, y + 13, true, width / 3 - 10, 0xc0c0c0
        );

        float scale = 1;
        List<String> lines;
        int descriptionHeight = option.getConfig().getDescriptionBehaviour(option) != DescriptionRendereringBehaviour.EXPAND_PANEL ? HEIGHT : getHeight();
        while (true) {
            lines = fr.splitText(option.getDescription(), (int) (width * 2 / 3 / scale - 10));
            if (lines.size() * scale * (fr.getHeight() + 1) + 10 < descriptionHeight)
                break;
            scale -= 1 / 8f;
            if (scale < 1 / 16f) break;
        }
        context.pushMatrix();
        context.translate(x + 5 + width / 3, y + 5, 0);
        context.scale(scale, scale, 1);
        context.translate(0, ((descriptionHeight - 10) - (fr.getHeight() + 1) * (lines.size() - 1) * scale) / 2F, 0);
        for (String line : lines) {
            context.drawString(fr, line, 0, 0, 0xc0c0c0, false);
            context.translate(0, fr.getHeight() + 1, 0);
        }
        context.popMatrix();
    }

    public int getHeight() {
        if (option.getConfig().getDescriptionBehaviour(option) != DescriptionRendereringBehaviour.EXPAND_PANEL)
            return HEIGHT;
        var fr = IMinecraft.instance.getDefaultFontRenderer();
        return Math.max(45, fr.splitText(option.getDescription(), 250 * 2 / 3 - 10).size() * (fr.getHeight() + 1) + 10);
    }

    @Deprecated
    protected boolean mouseInput(int x, int y, int width, int mouseX, int mouseY) {
        return false;
    }

    public boolean mouseInput(int x, int y, int width, int mouseX, int mouseY, MouseEvent mouseEvent) {
        return this.mouseInput(x, y, width, mouseX, mouseY);
    }

    @Deprecated
    protected boolean keyboardInput() {
        return false;
    }

    public boolean keyboardInput(KeyboardEvent event) {
        return keyboardInput();
    }

    public boolean mouseInputOverlay(int x, int y, int width, int mouseX, int mouseY, MouseEvent mouseEvent) {
        return false;
    }

    // TODO: add RenderContext
    public void renderOverlay(int x, int y, int width) {
    }

    public boolean fulfillsSearch(String word) {
        if (searchDescNameCache == null) {
            searchDescNameCache = (option.getName() + option.getDescription() + searchTags).toLowerCase(Locale.ROOT);
        }
        return searchDescNameCache.contains(word);
    }

    public void setGuiContext(GuiContext guiContext) {
    }
}
