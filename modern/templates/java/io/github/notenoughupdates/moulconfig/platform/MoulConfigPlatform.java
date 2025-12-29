package io.github.notenoughupdates.moulconfig.platform;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.notenoughupdates.moulconfig.common.*;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.internal.FilterAssertionCache;
import io.github.notenoughupdates.moulconfig.internal.MCLogger;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import kotlin.Pair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
#if MC < 12111
import net.minecraft.resources.ResourceLocation;
#else
import net.minecraft.resources.Identifier;
#endif
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@NullMarked
public class MoulConfigPlatform implements IMinecraft {
    public static @Nullable MoulConfigPlatform instance;
    Minecraft mc = Minecraft.getInstance();

    public MoulConfigPlatform() {
        if (instance != null) {
            Warnings.warn("Constructed duplicate MoulConfig instance");
        }
        instance = this;
    }

    //<editor-fold desc="Wrap / Unwrap helpers">
    public static #if MC < 12111 ResourceLocation #else Identifier #endif unwrap(MyResourceLocation resourceLocation) {
        #if MC < 12111
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getRoot(), resourceLocation.getPath());
        #else
        return Identifier.fromNamespaceAndPath(resourceLocation.getRoot(), resourceLocation.getPath());
        #endif
    }

    public static MyResourceLocation wrap(#if MC < 12111 ResourceLocation #else Identifier #endif identifier) {
        return new MyResourceLocation(identifier.getNamespace(), identifier.getPath());
    }

    public static ItemStack unwrap(IItemStack itemStack) {
        return ((MoulConfigItemStack) itemStack).getItemStack();
    }

    public static IItemStack wrap(ItemStack itemStack) {
        return new MoulConfigItemStack(itemStack);
    }

    public static Component unwrap(StructuredText structuredText) {
        return MoulConfigText.unwrap(structuredText);
    }

    public static StructuredText wrap(Component text) {
        return MoulConfigText.wrap(text);
    }

    public static StructuredText.Mutable wrap(MutableComponent text) {
        return MoulConfigText.wrap(text);
    }

    public static Font unwrap(IFontRenderer fontRenderer) {
        return ((MoulConfigFontRenderer) fontRenderer).getFont();
    }

    public static IFontRenderer wrap(Font font) {
        return new MoulConfigFontRenderer(font);
    }
    //</editor-fold>

    @SneakyThrows
    @Override
    public InputStream loadResourceLocation(MyResourceLocation resourceLocation) {
        return mc.getResourceManager()
            .getResourceOrThrow(unwrap(resourceLocation))
            .open();
    }

    @Override
    public MCLogger getLogger(String label) {
        Logger logger = LogManager.getLogger(label);
        return new MCLogger() {
            @Override
            public void warn(@NotNull String text) {
                logger.warn(text);
            }

            @Override
            public void info(@NotNull String text) {
                logger.info(text);
            }

            @Override
            public void error(@NotNull String text, @NotNull Throwable throwable) {
                logger.error(text, throwable);
            }
        };
    }

    @Override
    public boolean isGeneratedSentinel(MyResourceLocation resourceLocation) {
        return Objects.equals("moulconfig", resourceLocation.getRoot())
            && resourceLocation.getPath().startsWith("dynamic/");
    }

    private static void setTextureData(DynamicTexture texture, BufferedImage image) {
        var destinationImage = texture.getPixels();
        assert destinationImage != null;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                var argb = image.getRGB(i, j);
                destinationImage.setPixel(i, j, argb);
            }
        }
    }

    @Override
    public DynamicTextureReference generateDynamicTexture(BufferedImage img) {
        #if MC < 12111
        var identifier = ResourceLocation.fromNamespaceAndPath("moulconfig", "dynamic/${java.util.concurrent.ThreadLocalRandom.current().nextLong()}");
        #else
        var identifier = Identifier.fromNamespaceAndPath("moulconfig", "dynamic/${java.util.concurrent.ThreadLocalRandom.current().nextLong()}");
        #endif
        var texture = new DynamicTexture(#if MC>12104 identifier.getPath(), #endif img.getWidth(), img.getHeight(), true);
        setTextureData(texture, img);
        texture.upload();
        mc.getTextureManager().register(identifier, texture);
        return new DynamicTextureReference() {
            @Override
            public @NotNull MyResourceLocation getIdentifier() {
                return wrap(identifier);
            }

            @Override
            public void update(@NotNull BufferedImage bufferedImage) {
                setTextureData(texture, bufferedImage);
                texture.upload();
            }

            @Override
            protected void doDestroy() {
                FilterAssertionCache.destroyGlobalFilter(wrap(identifier));
                mc.getTextureManager().release(identifier);
            }
        };
    }

    @Override
    public Pair<Double, Double> getMousePositionHF() {
        var mouse = mc.mouseHandler;
        var window = mc.getWindow();
        #if MC < 12111
        var x = (mouse.xpos() * (double) window.getGuiScaledWidth() / window.getWidth());
        var y = (mouse.ypos() * (double) window.getGuiScaledHeight() / window.getHeight());
        #else
        double x = mouse.getScaledXPos(window);
        double y = mouse.getScaledYPos(window);
        #endif
        return new Pair<>(x, y);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public IFontRenderer getDefaultFontRenderer() {
        return new MoulConfigFontRenderer(mc.font);
    }

    @Override
    public IKeyboardConstants getKeyboardConstants() {
        return ModernKeyboardConstants.INSTANCE;
    }

    @Override
    public int getScaledWidth() {
        return mc.getWindow().getGuiScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return mc.getWindow().getGuiScaledHeight();
    }

    @Override
    public int getScaleFactor() {
        return (int) mc.getWindow().getGuiScale();
    }


    @Override
    public boolean isOnMacOs() {
        #if MC < 12109
        return Minecraft.ON_OSX;
        #elif MC < 12111
        return net.minecraft.Util.getPlatform() == net.minecraft.Util.OS.OSX;
        #else
        return net.minecraft.util.Util.getPlatform() == net.minecraft.util.Util.OS.OSX;
        #endif
    }

    @Override
    public boolean isMouseButtonDown(int mouseButton) {
        return GLFW.glfwGetMouseButton(#if MC < 12109 mc.getWindow().getWindow() #else mc.getWindow().handle() #endif, mouseButton) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean isKeyboardKeyDown(int keyboardKey) {
        return InputConstants.isKeyDown(#if MC < 12109 mc.getWindow().getWindow() #else mc.getWindow() #endif, keyboardKey);
    }

    @Override
    public void addExtraBuiltinConfigProcessors(MoulConfigProcessor<?> processor) {

    }

    @Override
    public void sendClickableChatMessage(StructuredText message, String action, @Nullable ClickType type) {
        var text = MoulConfigText.unwrap(message);
        if (type != null) {
            text = text.copy().withStyle(it -> it.withClickEvent(switch (type) {
                case OPEN_LINK -> #if MC > 12104 new ClickEvent.OpenUrl(URI.create(action)) #else new ClickEvent(ClickEvent.Action.OPEN_URL, action) #endif;
                case RUN_COMMAND -> #if MC > 12104 new ClickEvent.RunCommand(action) #else new ClickEvent(ClickEvent.Action.RUN_COMMAND, action) #endif;
            }));
        }
        mc.gui.getChat().addMessage(text);
    }

    @Override
    public StructuredText getKeyName(int keyCode) {
        return ModernKeybindHelper.getKeyName(keyCode);
    }

    @Override
    public StructuredText.Mutable createLiteral(String text) {
        return wrap(Component.literal(text));
    }

    @Override
    public StructuredText.Mutable createTranslatable(String key, StructuredText... args) {
        return wrap(Component.translatable(key, Stream.of(args).map(MoulConfigPlatform::unwrap).toArray()));
    }

    @Override
    public @Nullable StructuredText createStructuredTextInternal(Object obj) {
        if (obj instanceof Component text)
            return wrap(text);
        return null;
    }

    @ApiStatus.Internal
    public static GuiGraphics makeDrawContext() {
        var mc = Minecraft.getInstance();
        return new GuiGraphics(
            mc,
            #if MC >= 12107
            mc.gameRenderer.guiRenderState
            #else
            mc.renderBuffers().bufferSource()
            #endif
            #if MC >= 12111
            ,
            (int) mc.mouseHandler.getScaledXPos(mc.getWindow()),
            (int) mc.mouseHandler.getScaledYPos(mc.getWindow())
            #endif
        );
    }

    @Override
    public RenderContext provideTopLevelRenderContext() {
        return new MoulConfigRenderContext(makeDrawContext());
    }

    public void openWrappedScreen(Screen screen) {
        mc.setScreen(screen);
    }

    @Override
    public void openWrappedScreen(GuiContext gui) {
        openWrappedScreen(new MoulConfigScreenComponent(Component.empty(), gui, null));
    }

    @Override
    public void copyToClipboard(String string) {
        mc.keyboardHandler.setClipboard(string);
    }

    @Override
    public String copyFromClipboard() {
        return mc.keyboardHandler.getClipboard();
    }
}
