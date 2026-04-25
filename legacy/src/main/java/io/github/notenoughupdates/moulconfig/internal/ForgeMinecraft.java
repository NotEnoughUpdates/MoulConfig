package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorText;
import io.github.notenoughupdates.moulconfig.common.ClickType;
import io.github.notenoughupdates.moulconfig.common.DynamicTextureReference;
import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.IKeyboardConstants;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.MoulConfigPair;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiComponentWrapper;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.gui.GuiElement;
import io.github.notenoughupdates.moulconfig.gui.GuiScreenElementWrapper;
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorKeybindL;
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorSliderL;
import io.github.notenoughupdates.moulconfig.gui.editors.GuiOptionEditorTextL;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.event.ClickEvent;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ForgeMinecraft implements IMinecraft {
    @Override
    public InputStream loadResourceLocation(MyResourceLocation resourceLocation) {
        try {
            return Minecraft.getMinecraft().getResourceManager().getResource(fromMyResourceLocation(resourceLocation)).getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MCLogger getLogger(String label) {
        Logger logger = LogManager.getLogger(label);
        return new MCLogger() {
            @Override public void warn(String text) { logger.warn(text); }
            @Override public void info(String text) { logger.info(text); }
            @Override public void error(String text, Throwable throwable) { logger.error(text, throwable); }
        };
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addExtraBuiltinConfigProcessors(MoulConfigProcessor<?> processor) {
        processor.registerConfigEditor(ConfigEditorKeybind.class,
            (processedOption, keybind) -> new GuiOptionEditorKeybindL(processedOption, keybind.defaultKey()));
        processor.registerConfigEditor(ConfigEditorText.class,
            (processedOption, configEditorText) -> new GuiOptionEditorTextL(processedOption));
        processor.registerConfigEditor(ConfigEditorSlider.class,
            (processedOption, configEditorSlider) -> new GuiOptionEditorSliderL(
                processedOption,
                configEditorSlider.minValue(),
                configEditorSlider.maxValue(),
                configEditorSlider.minStep()
            ));
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    @Override
    public int getScaledWidth() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    @Override
    public int getScaleFactor() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    @Override
    public void sendClickableChatMessage(StructuredText message, String action, ClickType type) {
        IChatComponent component = StructuredTextImpl.unwrap(message);
        if (type != null) {
            ClickEvent.Action clickAction = type == ClickType.OPEN_LINK ? ClickEvent.Action.OPEN_URL : ClickEvent.Action.RUN_COMMAND;
            component.setChatStyle(component.getChatStyle().setChatClickEvent(new ClickEvent(clickAction, action)));
        }
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
    }

    @Override
    public DynamicTextureReference generateDynamicTexture(BufferedImage image) {
        DynamicTexture texture = new DynamicTexture(image);
        ResourceLocation res = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("moulconfigdyn", texture);
        return new DynamicTextureReference() {
            @Override
            public MyResourceLocation getIdentifier() {
                return fromResourceLocation(res);
            }

            @Override
            public void update(BufferedImage bufferedImage) {
                bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), texture.getTextureData(), 0, bufferedImage.getWidth());
                texture.updateDynamicTexture();
            }

            @Override
            protected void doDestroy() {
                Minecraft.getMinecraft().getTextureManager().deleteTexture(res);
            }
        };
    }

    @Override
    public boolean isGeneratedSentinel(MyResourceLocation resourceLocation) {
        return "moulconfigdyn".equals(resourceLocation.getRoot());
    }

    @Override
    public StructuredText getKeyName(int keyCode) {
        return StructuredText.of(KeybindHelper.getKeyName(keyCode));
    }

    @Override
    public StructuredText.Mutable createLiteral(String text) {
        return StructuredTextImpl.wrap(new ChatComponentText(text));
    }

    @Override
    public StructuredText.Mutable createTranslatable(String key, StructuredText... args) {
        return StructuredTextImpl.wrap(new ChatComponentTranslation(key, (Object[]) args));
    }

    @Override
    public StructuredText createStructuredTextInternal(Object obj) {
        if (obj instanceof IChatComponent) {
            return StructuredTextImpl.wrap((IChatComponent) obj);
        }
        return null;
    }

    @Override
    public void registerPlatformTypeMorphisms(XMLUniverse universe) {
    }

    @Override
    public boolean isMouseButtonDown(int mouseButton) {
        return Mouse.isButtonDown(mouseButton);
    }

    @Override
    public boolean isKeyboardKeyDown(int keyboardKey) {
        return Keyboard.isKeyDown(keyboardKey);
    }

    @Override
    public RenderContext provideTopLevelRenderContext() {
        return new ForgeRenderContext();
    }

    public void openScreen(GuiScreen gui) {
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    @Override
    public void openWrappedScreen(GuiElement gui) {
        openScreen(new GuiScreenElementWrapper(gui));
    }

    @Override
    public void openWrappedScreen(GuiContext gui) {
        openScreen(new GuiComponentWrapper(gui));
    }

    @Override
    public void copyToClipboard(String string) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(string), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String copyFromClipboard() {
        try {
            Object value = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
            return value instanceof String ? (String) value : "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public MoulConfigPair<Double, Double> getMousePositionHF() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        double width = sr.getScaledWidth_double();
        double mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth;
        double height = sr.getScaledHeight_double();
        double mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1;
        return new MoulConfigPair<>(mouseX, mouseY);
    }

    public static ResourceLocation fromMyResourceLocation(MyResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getRoot(), resourceLocation.getPath());
    }

    public static MyResourceLocation fromResourceLocation(ResourceLocation resourceLocation) {
        return new MyResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
    }

    @Override
    public boolean isOnMacOs() {
        return Minecraft.isRunningOnMac;
    }

    @Override
    public IFontRenderer getDefaultFontRenderer() {
        return new ForgeFontRenderer(Minecraft.getMinecraft().fontRendererObj);
    }

    @Override
    public IKeyboardConstants getKeyboardConstants() {
        return ForgeKeyboardConstants.INSTANCE;
    }
}
