package io.github.notenoughupdates.moulconfig.common;

import io.github.notenoughupdates.moulconfig.internal.NinePatchRenderer;
import juuxel.libninepatch.NinePatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.List;

public interface RenderContext {
    void pushMatrix();

    void popMatrix();

    void translate(float x, float y, float z);

    void scale(float x, float y, float z);

    void color(float r, float g, float b, float a);

    boolean isMouseButtonDown(int mouseButton);

    boolean isKeyboardKeyDown(int keyboardKey);

    default boolean isShiftDown() {
        return isKeyboardKeyDown(KeyboardConstants.INSTANCE.getShiftLeft()) || isKeyboardKeyDown(KeyboardConstants.INSTANCE.getShiftRight());
    }

    /**
     * Returns whether the control key is held down on Windows and Linux, and for macOS checks if the command key is held down.
     */
    default boolean isCtrlDown() {
        if (getMinecraft().isOnMacOS()) {
            return isKeyboardKeyDown(KeyboardConstants.INSTANCE.getCmdLeft()) || isKeyboardKeyDown(KeyboardConstants.INSTANCE.getCmdRight());
        } else {
            return isKeyboardKeyDown(KeyboardConstants.INSTANCE.getCtrlLeft()) || isKeyboardKeyDown(KeyboardConstants.INSTANCE.getCtrlRight());
        }
    }

    default void drawStringScaledMaxWidth(@NotNull String text, @NotNull IFontRenderer fontRenderer, int x, int y, boolean shadow, int width, int color) {
        pushMatrix();
        translate(x, y, 0);
        float scale = Math.min(1F, Math.max(0.1F, width / (float) fontRenderer.getStringWidth(text)));
        scale(scale, scale, 1F);
        drawString(fontRenderer, text, 0, 0, color, shadow);
        popMatrix();
    }

    default void drawStringCenteredScaledMaxWidth(
        @NotNull String text,
        @NotNull IFontRenderer fr,
        float x, float y,
        boolean shadow,
        int length, int color
    ) {
        pushMatrix();
        int strLength = fr.getStringWidth(text);
        float factor = Math.min(length / (float) strLength, 1f);
        translate(x, y, 0);
        scale(factor, factor, 1);
        drawString(fr, text, -strLength / 2, -fr.getHeight() / 2, color, shadow);
        popMatrix();
    }

    void disableDepth();

    void enableDepth();


    /**
     * Dynamically load a buffered image into a minecraft bindable texture. The returned resource location must be destroyed.
     *
     * @return a resource location that can be used with {@link #bindTexture}
     */
    @NotNull DynamicTextureReference generateDynamicTexture(@NotNull BufferedImage image);

    default void drawVerticalLine(int x, int startY, int endY, int color) {
        if (startY > endY) {
            int temp = startY;
            startY = endY;
            endY = temp;
        }
        drawColoredRect(x, startY + 1, x + 1, endY, color);
    }

    default void drawHorizontalLine(int y, int startX, int endX, int color) {
        if (startX > endX) {
            int temp = startX;
            startX = endX;
            endX = temp;
        }
        drawColoredRect(startX, y, endX + 1, y + 1, color);
    }


    void drawTriangles(float... coordinates);

    default void drawOpenCloseTriangle(boolean isOpen, float x, float y, float width, float height) {
        color(1, 1, 1, 1);
        if (isOpen) {
            drawTriangles(
                x, y,
                x + width / 2, y + height,
                x + width, y
            );
        } else {
            drawTriangles(
                x, y + height,
                x + width, y + height / 2,
                x, y
            );
        }
    }

    int drawString(@NotNull IFontRenderer fontRenderer, @NotNull String text, int x, int y, int color, boolean shadow);

    void drawColoredRect(float left, float top, float right, float bottom, int color);

    void invertedRect(float left, float top, float right, float bottom);

    default void drawTexturedRect(float x, float y, float width, float height) {
        drawTexturedRect(x, y, width, height, 0f, 0f, 1f, 1f);
    }

    void drawTexturedRect(float x, float y, float width, float height, float u1, float v1, float u2, float v2);


    enum TextureFilter {
        /**
         * Smoothly interpolate between pixels
         */
        LINEAR,
        /**
         * Do not interpolate between pixels (pixelated upscaling).
         */
        NEAREST;
    }

    /**
     * Set the texture min and mag filter which dictate how an image is upscaled / interpolated.
     * Affects the {@link #bindTexture currently bound texture}.
     */
    void setTextureMinMagFilter(@NotNull TextureFilter filter);

    default void drawNinePatch(@NotNull NinePatch<@NotNull MyResourceLocation> patch, float x, float y, int width, int height) {
        pushMatrix();
        translate(x, y, 0);
        patch.draw(NinePatchRenderer.INSTANCE, this, width, height);
        popMatrix();
    }

    default void drawDarkRect(int x, int y, int width, int height) {
        drawDarkRect(x, y, width, height, true);
    }

    void drawDarkRect(int x, int y, int width, int height, boolean shadow);

    void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor);

    void pushScissor(int left, int top, int right, int bottom);

    void popScissor();

    void clearScissor();

    void renderItemStack(@NotNull IItemStack itemStack, int x, int y, @Nullable String overlayText);

    void scheduleDrawTooltip(@NotNull List<String> tooltipLines);

    void doDrawTooltip();

    void refreshScissor();

    void disableScissor();


    default @NotNull IMinecraft getMinecraft() {
        return IMinecraft.instance;
    }

    default void bindTexture(@NotNull MyResourceLocation texture) {
        getMinecraft().bindTexture(texture);
    }
}
