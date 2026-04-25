package io.github.notenoughupdates.moulconfig.common;

import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.internal.MCLogger;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class MockIMinecraft implements IMinecraft {
    @Override public boolean isDevelopmentEnvironment() { return false; }
    @Override public MCLogger getLogger(String label) {
        return new MCLogger() {
            @Override public void warn(String text) {}
            @Override public void info(String text) {}
            @Override public void error(String text, Throwable throwable) {}
        };
    }
    @Override public InputStream loadResourceLocation(MyResourceLocation resourceLocation) { throw new UnsupportedOperationException(); }
    @Override public boolean isGeneratedSentinel(MyResourceLocation resourceLocation) { throw new UnsupportedOperationException(); }
    @Override public DynamicTextureReference generateDynamicTexture(BufferedImage image) { throw new UnsupportedOperationException(); }
    @Override public MoulConfigPair<Double, Double> getMousePositionHF() { throw new UnsupportedOperationException(); }
    @Override public IFontRenderer getDefaultFontRenderer() { throw new UnsupportedOperationException(); }
    @Override public IKeyboardConstants getKeyboardConstants() { throw new UnsupportedOperationException(); }
    @Override public int getScaledWidth() { throw new UnsupportedOperationException(); }
    @Override public int getScaledHeight() { throw new UnsupportedOperationException(); }
    @Override public int getScaleFactor() { throw new UnsupportedOperationException(); }
    @Override public boolean isOnMacOs() { throw new UnsupportedOperationException(); }
    @Override public boolean isMouseButtonDown(int mouseButton) { throw new UnsupportedOperationException(); }
    @Override public boolean isKeyboardKeyDown(int keyCode) { throw new UnsupportedOperationException(); }
    @Override public void addExtraBuiltinConfigProcessors(MoulConfigProcessor<?> processor) { throw new UnsupportedOperationException(); }
    @Override public void sendClickableChatMessage(StructuredText message, String action, ClickType clickType) { throw new UnsupportedOperationException(); }
    @Override public StructuredText getKeyName(int keyCode) { throw new UnsupportedOperationException(); }
    @Override public StructuredText.Mutable createLiteral(String text) { throw new UnsupportedOperationException(); }
    @Override public StructuredText.Mutable createTranslatable(String key, StructuredText... args) { throw new UnsupportedOperationException(); }
    @Override public StructuredText createStructuredTextInternal(Object object) { throw new UnsupportedOperationException(); }
    @Override public void registerPlatformTypeMorphisms(XMLUniverse universe) { throw new UnsupportedOperationException(); }
    @Override public RenderContext provideTopLevelRenderContext() { throw new UnsupportedOperationException(); }
    @Override public void openWrappedScreen(GuiContext guiContext) { throw new UnsupportedOperationException(); }
    @Override public void copyToClipboard(String string) { throw new UnsupportedOperationException(); }
    @Override public String copyFromClipboard() { throw new UnsupportedOperationException(); }
}
