package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.gui.GuiScreenElementWrapper;
import io.github.moulberry.moulconfig.gui.MoulConfigEditor;
import io.github.moulberry.moulconfig.struct.BuiltinMoulConfigGuis;
import io.github.moulberry.moulconfig.struct.ConfigProcessor;
import io.github.moulberry.moulconfig.struct.MoulConfigProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = "moulconfig")
public class MoulConfigTest {

    GuiScreen screenToOpen = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if (screenToOpen != null) {
            Minecraft.getMinecraft().displayGuiScreen(screenToOpen);
            screenToOpen = null;
        }
    }

    public static TestConfig testConfig = new TestConfig();
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(MoulConfigTest.this);
        MoulConfigProcessor<TestConfig> testConfigMoulConfigProcessor = new MoulConfigProcessor<>(testConfig);
        BuiltinMoulConfigGuis.addProcessors(testConfigMoulConfigProcessor);
        ConfigProcessor.processConfig(testConfig.getClass(), testConfigMoulConfigProcessor);
        testConfig.testCategory.text2.whenChanged((oldValue, newValue) ->
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentText("Just changed text2 from " + oldValue + " to " + newValue)));
        ClientCommandHandler.instance.registerCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return "moulconfig";
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "moulconfig";
            }

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender sender) {
                return true;
            }

            @Override
            public void processCommand(ICommandSender sender, String[] args) {
                sender.addChatMessage(new ChatComponentText("Mouling"));
                screenToOpen = new GuiScreenElementWrapper(new MoulConfigEditor<>(testConfigMoulConfigProcessor));
            }
        });
    }
}
