package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.*;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiComponentWrapper;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.gui.GuiElement;
import io.github.notenoughupdates.moulconfig.gui.GuiElementWrapper;
import io.github.notenoughupdates.moulconfig.internal.FilterAssertionCache;
import io.github.notenoughupdates.moulconfig.internal.MCLogger;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import kotlin.Pair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    public MoulConfigPlatform() {
        if (instance != null) {
            Warnings.warn("Constructed duplicate MoulConfig instance");
        }
        instance = this;
    }

    //<editor-fold desc="Wrap / Unwrap helpers">
    public static Identifier unwrap(MyResourceLocation resourceLocation) {
        return Identifier.of(resourceLocation.getRoot(), resourceLocation.getPath());
    }

    public static MyResourceLocation wrap(Identifier identifier) {
        return new MyResourceLocation(identifier.getNamespace(), identifier.getPath());
    }

    public static Text unwrap(StructuredText structuredText) {
        return MoulConfigText.unwrap(structuredText);
    }

    public static StructuredText wrap(Text text) {
        return MoulConfigText.wrap(text);
    }
    //</editor-fold>

    MinecraftClient mc = MinecraftClient.getInstance();

    @SneakyThrows
    @Override
    public InputStream loadResourceLocation(MyResourceLocation resourceLocation) {
        return mc.getResourceManager()
            .getResourceOrThrow(unwrap(resourceLocation))
            .getInputStream();
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

    private static void setTextureData(NativeImageBackedTexture texture, BufferedImage image) {
        var destinationImage = texture.getImage();
        assert destinationImage != null;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                var argb = image.getRGB(i, j);
                destinationImage.setColorArgb(i, j, argb);
            }
        }
    }

    @Override
    public DynamicTextureReference generateDynamicTexture(BufferedImage img) {
        var identifier = Identifier.of("moulconfig", "dynamic/${java.util.concurrent.ThreadLocalRandom.current().nextLong()}");
        var texture = new NativeImageBackedTexture(identifier.getPath(), img.getWidth(), img.getHeight(), true);
        setTextureData(texture, img);
        texture.upload();
        mc.getTextureManager().registerTexture(identifier, texture);
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
                mc.getTextureManager().destroyTexture(identifier);
            }
        };
    }

    @Override
    public Pair<Double, Double> getMousePositionHF() {
        var mouse = mc.mouse;
        var window = mc.getWindow();
        // TODO: on newer versions we can use mouse.getScaledY() directly. would be a place for a preprocessor
        var y = (mouse.getY() * (double) window.getScaledHeight() / window.getHeight());
        var x = (mouse.getX() * (double) window.getScaledWidth() / window.getWidth());
        return new Pair<>(x, y);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public IFontRenderer getDefaultFontRenderer() {
        return new MoulConfigFontRenderer(mc.textRenderer);
    }

    @Override
    public IKeyboardConstants getKeyboardConstants() {
        return ModernKeyboardConstants.INSTANCE;
    }

    @Override
    public int getScaledWidth() {
        return mc.getWindow().getScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return mc.getWindow().getScaledHeight();
    }

    @Override
    public int getScaleFactor() {
        return mc.getWindow().getScaleFactor();
    }


    @Override
    public boolean isOnMacOs() {
        return MinecraftClient.IS_SYSTEM_MAC;
    }

    @Override
    public boolean isMouseButtonDown(int mouseButton) {
        return GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), mouseButton) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean isKeyboardKeyDown(int keyboardKey) {
        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), keyboardKey);
    }

    @Override
    public void addExtraBuiltinConfigProcessors(MoulConfigProcessor<?> processor) {

    }

    @Override
    public void sendClickableChatMessage(StructuredText message, String action, @Nullable ClickType type) {
        var text = MoulConfigText.unwrap(message);
        if (type != null) {
            text = text.copy().styled(it -> it.withClickEvent(switch (type) {
                case OPEN_LINK -> new ClickEvent.OpenUrl(URI.create(action));
                case RUN_COMMAND -> new ClickEvent.RunCommand(action);
            }));
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
    }

    @Override
    public StructuredText getKeyName(int keyCode) {
        return ModernKeybindHelper.INSTANCE.getKeyName(keyCode);
    }

    @Override
    public StructuredText createLiteral(String text) {
        return wrap(Text.literal(text));
    }

    @Override
    public StructuredText createTranslatable(String key, StructuredText... args) {
        return wrap(Text.translatable(key, Stream.of(args).map(MoulConfigPlatform::unwrap).toArray()));
    }

    @Override
    public @Nullable StructuredText createStructuredTextInternal(Object obj) {
        if (obj instanceof Text text)
            return wrap(text);
        return null;
    }

    @ApiStatus.Internal
    public static DrawContext makeDrawContext() {
        return new DrawContext(
            MinecraftClient.getInstance(),
            MinecraftClient.getInstance().gameRenderer.guiState
        );
    }

    @Override
    public RenderContext provideTopLevelRenderContext() {
        return new ModernRenderContext(makeDrawContext());
    }

    public void openWrappedScreen(Screen screen) {
        mc.setScreen(screen);
    }

    @Override
    public void openWrappedScreen(GuiElement gui) {
        openWrappedScreen(new GuiElementWrapper(gui));
    }

    @Override
    public void openWrappedScreen(GuiContext gui) {
        openWrappedScreen(new GuiComponentWrapper(gui));
    }

    @Override
    public void copyToClipboard(String string) {
        mc.keyboard.setClipboard(string);
    }

    @Override
    public String copyFromClipboard() {
        return mc.keyboard.getClipboard();
    }
}
