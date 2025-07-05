package io.github.notenoughupdates.moulconfig.common;

import io.github.notenoughupdates.moulconfig.internal.NinePatchRenderer;
import juuxel.libninepatch.NinePatch;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface RenderContext {
    void pushMatrix();

    void popMatrix();

    void translate(float x, float y);

    void scale(float x, float y);

    default void scale(float scalar) {
        scale(scalar, scalar);
    }

    enum ScissorBehaviour {
        ESCAPE,
        INHERIT,
    }

    /**
     * draws more content that should be laid on top of other later render calls. the consumer will be invoked linearly, but with no guarantee for when.
     */
    void drawOnTop(@NotNull Layer layer, @NotNull ScissorBehaviour escapeScissors, @NotNull Consumer<@NotNull RenderContext> later);

    boolean isMouseButtonDown(int mouseButton);

    boolean isKeyboardKeyDown(int keyboardKey);

    default boolean isShiftDown() {
        return isKeyboardKeyDown(KeyboardConstants.INSTANCE.getShiftLeft()) || isKeyboardKeyDown(KeyboardConstants.INSTANCE.getShiftRight());
    }

    default boolean isPhysicalCtrlDown() {
        return isKeyboardKeyDown(KeyboardConstants.INSTANCE.getCtrlLeft()) || isKeyboardKeyDown(KeyboardConstants.INSTANCE.getCtrlRight());
    }

    /**
     * @return if the command (on 🍎) or super (on 🐧) or windows (on 🪟) key is down.
     */
    default boolean isCmdDown() {
        return isKeyboardKeyDown(KeyboardConstants.INSTANCE.getCmdLeft()) || isKeyboardKeyDown(KeyboardConstants.INSTANCE.getCmdRight());
    }

    /**
     * Returns whether the control key is held down on Windows and Linux, and for macOS checks if the command key is held down.
     */
    default boolean isLogicalCtrlDown() {
        if (getMinecraft().isOnMacOS()) {
            return isCmdDown();
        } else {
            return isPhysicalCtrlDown();
        }
    }

    default void drawStringScaledMaxWidth(@NotNull String text, @NotNull IFontRenderer fontRenderer, int x, int y, boolean shadow, int width, int color) {
        pushMatrix();
        translate(x, y);
        float scale = Math.min(1F, Math.max(0.1F, width / (float) fontRenderer.getStringWidth(text)));
        scale(scale, scale);
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
        translate(x, y);
        scale(factor, factor);
        drawString(fr, text, -strLength / 2, -fr.getHeight() / 2, color, shadow);
        popMatrix();
    }

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

    void drawColoredTriangles(int color, float... coordinates);

    default void drawOpenCloseTriangle(boolean isOpen, float x, float y, float width, float height, int color) {
        if (isOpen) {
            drawColoredTriangles(
                color,
                x, y,
                x + width / 2, y + height,
                x + width, y
            );
        } else {
            drawColoredTriangles(
                color,
                x, y + height,
                x + width, y + height / 2,
                x, y
            );
        }
    }

    int drawString(@NotNull IFontRenderer fontRenderer, @NotNull String text, int x, int y, int color, boolean shadow);

    void drawColoredRect(float left, float top, float right, float bottom, int color);

    void invertedRect(float left, float top, float right, float bottom, int additiveColor); // TODO: worth a consideration (is this a stable API)???

    default void drawTexturedRect(@NotNull MyResourceLocation texture, float x, float y, float width, float height) {
        drawComplexTexture(texture, x, y, width, height, drawTextureBuilder -> {
        });
    }

    void drawTexturedTintedRect(@NotNull MyResourceLocation texture,
                                float x, float y, float width, float height,
                                float u1, float v1, float u2, float v2,
                                int color, TextureFilter filter);

    class DrawTextureBuilder {
        @NotNull MyResourceLocation texture;
        float x;
        float y;
        float width;
        float height;
        float u1 = 0, v1 = 0, u2 = 1, v2 = 1;
        int color = -1;
        TextureFilter filter = TextureFilter.NEAREST;

        public DrawTextureBuilder(@NotNull MyResourceLocation texture, float x, float y, float width, float height) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public DrawTextureBuilder uv(float u1, float v1, float u2, float v2) {
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
            return this;
        }

        public DrawTextureBuilder color(int color) {
            this.color = color;
            return this;
        }

        public DrawTextureBuilder filter(TextureFilter filter) {
            this.filter = filter;
            return this;
        }

        public void applyTo(RenderContext renderContext) {
            renderContext.drawTexturedTintedRect(texture, x, y, width, height, u1, v1, u2, v2, color, filter);
        }
    }


    default void drawComplexTexture(@NotNull MyResourceLocation texture, float x, float y, float width, float height, Consumer<DrawTextureBuilder> block) {
        DrawTextureBuilder drawBuilder = new DrawTextureBuilder(texture, x, y, width, height);
        block.accept(drawBuilder);
        drawBuilder.applyTo(this);
    }

    default void drawNinePatch(@NotNull NinePatch<@NotNull MyResourceLocation> patch, float x, float y, int width, int height) {
        pushMatrix();
        translate(x, y);
        patch.draw(NinePatchRenderer.INSTANCE, this, width, height);
        popMatrix();
    }

    default void drawDarkRect(int x, int y, int width, int height) {
        drawDarkRect(x, y, width, height, true);
    }

    void drawDarkRect(int x, int y, int width, int height, boolean shadow);

    void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor);

    void pushScissor(int left, int top, int right, int bottom); // TODO: scissors should probably be pushed relative, like everything else.

    void popScissor();

    @Deprecated
    void clearScissor();  // TODO: this sort of escapes out of the current context.

    void renderItemStack(@NotNull IItemStack itemStack, int x, int y, @Nullable String overlayText);

    void drawTooltipNow(int x, int y, @NotNull List<String> tooltipLines);

    default void scheduleDrawTooltip(int x, int y, @NotNull List<String> tooltipLines) {
        // TODO: should this do some form of conflict resolution?
        drawOnTop(Layer.TOOLTIP, ScissorBehaviour.ESCAPE, it -> it.drawTooltipNow(x, y, tooltipLines));
    }

    /**
     * Must be called only by the implementor after all rendering is done.
     */
    void renderExtraLayers();

    default @NotNull IMinecraft getMinecraft() {
        return IMinecraft.instance;
    }
}
