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

package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.Social;
import io.github.moulberry.moulconfig.annotations.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class TestConfig extends Config {
    @Category(name = "Test Category", desc = "Test Description")
    public TestCategory testCategory = new TestCategory();

    @Category(name = "Test Category 2", desc = "Test Description 2")
    public TestCategory test = new TestCategory();

    @Override
    public void executeRunnable(int runnableId) {
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Just executed runnableId " + runnableId));
    }

    @Override
    public List<Social> getSocials() {
        return List.of(Social.forLink("Go to Discord", GuiTextures.RESET, "https://discord.gg/moulberry"));
    }

    @Override
    public boolean shouldAutoFocusSearchbar() {
        return true;
    }

    @Override
    public Text getTitle() {
        return Text.literal("§bMoulConfig §aTest §eConfig");
    }
}
