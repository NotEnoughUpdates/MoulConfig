package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.gui.GuiScreenElementWrapper;
import io.github.moulberry.moulconfig.gui.MoulConfigEditor;
import io.github.moulberry.moulconfig.gui.MoulGuiOverlayEditor;
import io.github.moulberry.moulconfig.internal.RenderUtils;
import io.github.moulberry.moulconfig.processor.BuiltinMoulConfigGuis;
import io.github.moulberry.moulconfig.processor.ConfigProcessorDriver;
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

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

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            TestCategory.TestOverlay o = testConfig.testCategory.testOverlay;
            GlStateManager.pushMatrix();
            o.transform();
            RenderUtils.drawFloatingRect(0, 0, o.getWidth(), o.getHeight());
            int mx = Mouse.getX();
            int my = Minecraft.getMinecraft().displayHeight - Mouse.getY() - 1;
            FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
            fontRendererObj.drawSplitString(
                "Global Mouse X: " + mx + "\n" +
                    "Global Mouse X: " + my + "\n" +
                    "Local Mouse X: " + o.realWorldXToLocalX(mx) + "\n" +
                    "Local Mouse Y: " + o.realWorldYToLocalY(my) + "\n" +
                    "Width: " + o.getWidth() + "\n" +
                    "Height: " + o.getHeight(),
                1, 1, o.getWidth(), 0xFFFFFFFF
            );
            GlStateManager.popMatrix();
        }
    }

    public static TestConfig testConfig = new TestConfig();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(MoulConfigTest.this);
        MoulConfigProcessor<TestConfig> testConfigMoulConfigProcessor = new MoulConfigProcessor<>(testConfig);
        BuiltinMoulConfigGuis.addProcessors(testConfigMoulConfigProcessor);
        ConfigProcessorDriver.processConfig(testConfig.getClass(), testConfig, testConfigMoulConfigProcessor);
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
                if (args.length > 0 && "gui".equals(args[0])) {
                    screenToOpen = new MoulGuiOverlayEditor(testConfigMoulConfigProcessor);
                } else {
                    screenToOpen = new GuiScreenElementWrapper(new MoulConfigEditor<>(testConfigMoulConfigProcessor));
                }
            }
        });
    }
}
