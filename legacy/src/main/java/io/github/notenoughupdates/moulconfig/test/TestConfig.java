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

package io.github.notenoughupdates.moulconfig.test;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.Social;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import io.github.notenoughupdates.moulconfig.gui.HorizontalAlign;
import io.github.notenoughupdates.moulconfig.processor.ProcessedCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;

public class TestConfig extends Config {
    @Category(name = "Test Category", desc = "Test Description")
    @Expose
    public TestCategory testCategory = new TestCategory();
    @Category(name = "Parent category", desc = "pArenting")
    @Expose
    public TestCategoryParent parent = new TestCategoryParent();

    @Override
    public HorizontalAlign alignCategory(ProcessedCategory category, boolean isSelected) {
        return HorizontalAlign.RIGHT;
    }

    @Override
    public String formatCategoryName(ProcessedCategory category, boolean isSelected) {
        return super.formatCategoryName(category, isSelected) + "AAAAAAAAAAAA";
    }

    @Override
    public void executeRunnable(int runnableId) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Just executed runnableId " + runnableId));
    }

    @Override
    public List<Social> getSocials() {
        return Arrays.asList(Social.forLink("Go to Discord", GuiTextures.RESET, "https://discord.gg/moulberry"));
    }

    @Override
    public boolean shouldAutoFocusSearchbar() {
        return true;
    }

    @Override
    public String getTitle() {
        return "§bMoulConfig §aTest §eConfig";
    }
}
