package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.Social;
import io.github.moulberry.moulconfig.annotations.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;

public class TestConfig extends Config {
    @Category(name = "Test Category", desc = "Test Description")
    public TestCategory testCategory = new TestCategory();

    @Override
    public void executeRunnable(int runnableId) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Just executed runnableId " + runnableId));
    }

    @Override
    public List<Social> getSocials() {
        return Arrays.asList(Social.forLink("Go to Discord", GuiTextures.RESET, "https://discord.gg/moulberry"));
    }

    @Override
    public String getTitle() {
        return "§bMoulConfig §aTest §eConfig";
    }
}
