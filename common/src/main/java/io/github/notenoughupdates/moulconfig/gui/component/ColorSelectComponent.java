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

package io.github.notenoughupdates.moulconfig.gui.component;

import io.github.notenoughupdates.moulconfig.ChromaColour;
import io.github.notenoughupdates.moulconfig.GuiTextures;
import io.github.notenoughupdates.moulconfig.common.DynamicTextureReference;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext;
import io.github.notenoughupdates.moulconfig.gui.KeyboardEvent;
import io.github.notenoughupdates.moulconfig.gui.MouseEvent;
import io.github.notenoughupdates.moulconfig.internal.DrawContextExt;
import io.github.notenoughupdates.moulconfig.internal.LerpUtils;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorSelectComponent extends GuiComponent {
    private final TextFieldComponent componentHexField = new TextFieldComponent(
        new GetSetter<String>() {
            String editingBuffer = "";

            @Override
            public String get() {
                if (componentHexField.isFocused())
                    return editingBuffer;
                int rgb = ChromaColour.specialToSimpleRGB(colour);
                return editingBuffer = String.format("#%06x", rgb & 0xFFFFFF).toUpperCase();
            }

            private final Pattern validHex = Pattern.compile("^#([0-9a-fA-F]{1,6})$");

            @Override
            public void set(String newValue) {
                editingBuffer = newValue;
                Matcher matcher = validHex.matcher(newValue);
                if (matcher.matches()) {
                    int newRGB = Integer.parseInt(matcher.group(1), 16);
                    int alpha = (ChromaColour.specialToSimpleRGB(colour) >> 24) & 0xFF;
                    colour = ChromaColour.special(ChromaColour.getSpeed(colour), alpha, newRGB);
                    colourChangedCallback.accept(colour);

                    Color c = new Color(newRGB);
                    float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                    updateAngleAndRadius(hsv);
                }
            }
        },
        48,
        GetSetter.constant(true),
        "#000000",
        IMinecraft.instance.getDefaultFontRenderer()
    );

    private int xSize = 119;
    private final int ySize = 89;

    private float wheelAngle = 0;
    private float wheelRadius = 0;

    private final Consumer<String> colourChangedCallback;
    private final Runnable closeCallback;
    private String colour;

    private final boolean opacitySlider;
    private final boolean valueSlider;

    @Override
    public int getWidth() {
        return xSize;
    }

    @Override
    public int getHeight() {
        return ySize;
    }

    //TODO: refine those constructors
    public ColorSelectComponent(
        int x, int y, String initialColour, Consumer<String> colourChangedCallback,
        Runnable closeCallback
    ) {
        this(x, y, initialColour, colourChangedCallback, closeCallback, true, true);
    }

    public ColorSelectComponent(
        int x, int y, String initialColour, Consumer<String> colourChangedCallback,
        Runnable closeCallback, boolean opacitySlider, boolean valueSlider
    ) {

        this.colour = initialColour;
        this.colourChangedCallback = colourChangedCallback;
        this.closeCallback = closeCallback;

        int colour = ChromaColour.specialToSimpleRGB(initialColour);
        Color c = new Color(colour);
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        updateAngleAndRadius(hsv);

        this.opacitySlider = opacitySlider;
        this.valueSlider = valueSlider;

        if (!valueSlider) xSize -= 15;
        if (!opacitySlider) xSize -= 15;
    }

    public void updateAngleAndRadius(float[] hsv) {
        this.wheelRadius = hsv[1];
        this.wheelAngle = hsv[0] * 360;
    }

    private static DynamicTextureReference hueWheelImage;
    private static DynamicTextureReference brightnessSlider;
    private static DynamicTextureReference opacitySliderRef;

    private static DynamicTextureReference loadOrUpdate(RenderContext renderContext, DynamicTextureReference ref, BufferedImage image) {
        if (ref == null)
            return renderContext.generateDynamicTexture(image);
        ref.update(image);
        return ref;
    }

    private DynamicTextureReference getOpacitySlider(RenderContext renderContext, int currentColour) {
        BufferedImage bufferedImageOpacity = new BufferedImage(10, 64, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 64; y++) {
                if ((x == 0 || x == 9) && (y == 0 || y == 63)) continue;

                int rgb = (currentColour & 0x00FFFFFF) | (Math.min(255, (64 - y) * 4) << 24);
                bufferedImageOpacity.setRGB(x, y, rgb);
            }
        }
        return opacitySliderRef = loadOrUpdate(renderContext, opacitySliderRef, bufferedImageOpacity);
    }

    private DynamicTextureReference getBrightnessSlider(RenderContext renderContext) {
        BufferedImage bufferedImageValue = new BufferedImage(10, 64, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 64; y++) {
                if ((x == 0 || x == 9) && (y == 0 || y == 63)) continue;

                int rgb = Color.getHSBColor(wheelAngle / 360, wheelRadius, (64 - y) / 64f).getRGB();
                bufferedImageValue.setRGB(x, y, rgb);
            }
        }
        return brightnessSlider = loadOrUpdate(renderContext, brightnessSlider, bufferedImageValue);
    }

    private DynamicTextureReference getHueWheelImage(RenderContext renderContext, float brightness) {
        // TODO cache this image if parameters havent changed.
        BufferedImage bufferedImage = new BufferedImage(288, 288, BufferedImage.TYPE_INT_ARGB);
        float borderRadius = 0.05f;
        for (int x = -16; x < 272; x++) {
            for (int y = -16; y < 272; y++) {
                float radius = (float) Math.sqrt(((x - 128) * (x - 128) + (y - 128) * (y - 128)) / 16384f);
                float angle = (float) Math.toDegrees(Math.atan((128 - x) / (y - 128 + 1E-5)) + Math.PI / 2);
                if (y < 128) angle += 180;
                if (radius <= 1) {
                    int rgb = Color.getHSBColor(angle / 360f, (float) Math.pow(radius, 1.5f), brightness).getRGB();
                    bufferedImage.setRGB(x + 16, y + 16, rgb);
                } else if (radius <= 1 + borderRadius) {
                    float invBlackAlpha = Math.abs(radius - 1 - borderRadius / 2) / borderRadius * 2;
                    float blackAlpha = 1 - invBlackAlpha;

                    if (radius > 1 + borderRadius / 2) {
                        bufferedImage.setRGB(x + 16, y + 16, (int) (blackAlpha * 255) << 24);
                    } else {
                        Color col = Color.getHSBColor(angle / 360f, 1, brightness);
                        int rgb = (int) (col.getRed() * invBlackAlpha) << 16 |
                            (int) (col.getGreen() * invBlackAlpha) << 8 |
                            (int) (col.getBlue() * invBlackAlpha);
                        bufferedImage.setRGB(x + 16, y + 16, 0xff000000 | rgb);
                    }

                }
            }
        }
        return hueWheelImage = loadOrUpdate(renderContext, hueWheelImage, bufferedImage);
    }

    @Override
    public void render(@NotNull GuiImmediateContext context) {
        int currentColour = ChromaColour.specialToSimpleRGB(colour);
        Color c = new Color(currentColour, true);
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

        val renderContext = context.getRenderContext();
        renderContext.drawDarkRect(0, 0, xSize, ySize);

        float selradius = (float) Math.pow(wheelRadius, 1 / 1.5f) * 32;
        int selx = (int) (Math.cos(Math.toRadians(wheelAngle)) * selradius);
        int sely = (int) (Math.sin(Math.toRadians(wheelAngle)) * selradius);

        int valueOffset = 0;
        if (valueSlider) {
            valueOffset = 15;

            renderContext.bindTexture(getBrightnessSlider(renderContext).getIdentifier());
            renderContext.color(1, 1, 1, 1);
            renderContext.drawTexturedRect(5 + 64 + 5, 5, 10, 64);
        }

        int opacityOffset = 0;
        if (opacitySlider) {
            opacityOffset = 15;

            // Render checkerboard background
            renderContext.bindTexture(GuiTextures.COLOUR_SELECTOR_BAR_ALPHA);
            renderContext.color(1, 1, 1, 1);
            renderContext.drawTexturedRect(5 + 64 + 5 + valueOffset, 5, 10, 64);


            // Render actual color slider
            renderContext.bindTexture(getOpacitySlider(renderContext, currentColour).getIdentifier());
            renderContext.color(1, 1, 1, 1);
            renderContext.drawTexturedRect(5 + 64 + 5 + valueOffset, 5, 10, 64);
        }


        int chromaSpeed = ChromaColour.getSpeed(colour);
        int currentColourChroma = ChromaColour.specialToChromaRGB(colour);
        Color cChroma = new Color(currentColourChroma, true);
        float[] hsvChroma = Color.RGBtoHSB(cChroma.getRed(), cChroma.getGreen(), cChroma.getBlue(), null);

        if (chromaSpeed > 0) {
            renderContext.drawColoredRect(
                5 + 64 + valueOffset + opacityOffset + 5 + 1,
                5 + 1,
                5 + 64 + valueOffset + opacityOffset + 5 + 10 - 1, 5 + 64 - 1,
                Color.HSBtoRGB(hsvChroma[0], 0.8f, 0.8f)
            );
        } else {
            renderContext.drawColoredRect(
                5 + 64 + valueOffset + opacityOffset + 5 + 1,
                5 + 27 + 1,
                5 + 64 + valueOffset + opacityOffset + 5 + 10 - 1,
                5 + 37 - 1,
                Color.HSBtoRGB((float) (((double) hsvChroma[0] + (double) System.currentTimeMillis() / 1000.0) % 1), 0.8f, 0.8f)
            );
        }

        renderContext.bindTexture(GuiTextures.COLOUR_SELECTOR_BAR);
        renderContext.color(1, 1, 1, 1);
        if (valueSlider) renderContext.drawTexturedRect(5 + 64 + 5, 5, 10, 64);
        if (opacitySlider) renderContext.drawTexturedRect(5 + 64 + 5 + valueOffset, 5, 10, 64);


        if (chromaSpeed > 0) {
            renderContext.drawTexturedRect(5 + 64 + valueOffset + opacityOffset + 5, 5, 10, 64);
        } else {
            renderContext.bindTexture(GuiTextures.COLOUR_SELECTOR_CHROMA);
            renderContext.drawTexturedRect(5 + 64 + valueOffset + opacityOffset + 5, 5 + 27, 10, 10);
        }
        if (valueSlider)
            renderContext.drawColoredRect(
                5 + 64 + 5, 5 + 64 - (int) (64 * hsv[2]),
                5 + 64 + valueOffset, 5 + 64 - (int) (64 * hsv[2]) + 1,
                0xFF000000
            );
        if (opacitySlider)
            renderContext.drawColoredRect(
                5 + 64 + 5 + valueOffset,
                5 + 64 - c.getAlpha() / 4 - 1,
                5 + 64 + valueOffset + opacityOffset,
                5 + 64 - c.getAlpha() / 4, 0xFF000000
            );
        if (chromaSpeed > 0) {
            renderContext.drawColoredRect(
                5 + 64 + valueOffset + opacityOffset + 5,
                5 + 64 - (int) (chromaSpeed / 255f * 64),
                5 + 64 + valueOffset + opacityOffset + 5 + 10,
                5 + 64 - (int) (chromaSpeed / 255f * 64) + 1, 0xFF000000
            );
        }

        renderContext.bindTexture(getHueWheelImage(renderContext, hsv[2]).getIdentifier());
        renderContext.color(1, 1, 1, 1);
        renderContext.setTextureMinMagFilter(RenderContext.TextureFilter.LINEAR);
        renderContext.drawTexturedRect(1, 1, 72, 72);

        renderContext.bindTexture(GuiTextures.COLOUR_SELECTOR_DOT);
        renderContext.color(1, 1, 1, 1);
        renderContext.setTextureMinMagFilter(RenderContext.TextureFilter.LINEAR);
        renderContext.drawTexturedRect(5 + 32 + selx - 4, 5 + 32 + sely - 4, 8, 8);

        DrawContextExt.drawStringCenteredScalingDownWithMaxWidth(
            renderContext,
            "§7" + Math.round(hsv[2] * 100),
            5 + 64 + 5 + 5 - (Math.round(hsv[2] * 100) == 100 ? 1 : 0),
            5 + 64 + 5 + 5,
            13,
            -1,
            true
        );

        if (opacitySlider) {
            DrawContextExt.drawStringCenteredScalingDownWithMaxWidth(
                renderContext,
                "§7" + Math.round(c.getAlpha() / 255f * 100) + "",
                5 + 64 + 5 + valueOffset + 5,
                5 + 64 + 5 + 5,
                13,
                -1,
                true
            );
        }
        if (chromaSpeed > 0) {
            DrawContextExt.drawStringCenteredScalingDownWithMaxWidth(
                renderContext,
                "§7" +
                    (int) ChromaColour.getSecondsForSpeed(chromaSpeed) + "s",
                5 + 64 + 5 + valueOffset + opacityOffset + 6,
                5 + 64 + 5 + 5,
                13,
                -1,
                true
            );
        }

        renderContext.pushMatrix();
        renderContext.translate(5 + 8, 5 + 64 + 5, 0);
        componentHexField.render(context.translated(5 + 8, 5 + 64 + 5, 48, 12));
        renderContext.popMatrix();
    }

    enum ClickedComponent {
        HUE, BRIGHTNESS, OPACITY, CHROMA
    }

    private void updateOnMouseMovement(GuiImmediateContext context) {
        if (focusedSubComponent == null) {
            return;
        }
        int currentColour = ChromaColour.specialToSimpleRGB(colour);
        Color c = new Color(currentColour, true);
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float percentage = LerpUtils.clampZeroOne(((context.getMouseYHF() - 5) / 64F));
        switch (focusedSubComponent) {
            case BRIGHTNESS: {
                int rgb = Color.getHSBColor(wheelAngle / 360, wheelRadius, 1 - percentage).getRGB();
                colour = ChromaColour.special(ChromaColour.getSpeed(colour), c.getAlpha(), rgb);
                break;
            }
            case HUE: {
                float diffX = context.getMouseXHF() - 1 - 36;
                float diffY = context.getMouseYHF() - 1 - 36;
                float angle = (float) Math.toDegrees(Math.atan2(diffY, diffX));
                float radius = (float) Math.sqrt(diffX * diffX + diffY * diffY) / 32;
                this.wheelAngle = angle;
                this.wheelRadius = (float) Math.pow(Math.min(1, radius), 1.5F);
                int rgb = Color.getHSBColor(angle / 360F, wheelRadius, hsv[2]).getRGB();
                colour = ChromaColour.special(ChromaColour.getSpeed(colour), c.getAlpha(), rgb);
                break;
            }
            case CHROMA: {
                colour = ChromaColour.special(255 - Math.round(percentage * 255), c.getAlpha(), currentColour);
                break;
            }
            case OPACITY: {
                colour = ChromaColour.special(ChromaColour.getSpeed(colour),
                    255 - Math.round(percentage * 255), currentColour
                );
                break;
            }
        }
        colourChangedCallback.accept(colour);
    }

    private ClickedComponent focusedSubComponent = null;

    @Override
    public boolean mouseEvent(@NotNull MouseEvent mouseEvent, @NotNull GuiImmediateContext context) {
        if (mouseEvent instanceof MouseEvent.Click) {
            val click = (MouseEvent.Click) mouseEvent;
            if (!context.isHovered() && click.getMouseState()) {
                closeCallback.run(); // TODO: lift this out of the overlay, into the overlay handler
                return true;
            }
            if (context.isHovered() && click.getMouseState())
                requestFocus();
            if (focusedSubComponent != null && !click.getMouseState() && click.getMouseButton() == 0) {
                focusedSubComponent = null;
                return true;
            }
            int currentColour = ChromaColour.specialToSimpleRGB(colour);
            Color c = new Color(currentColour, true);
            if (click.getMouseState()) {
                int centerX = 1 + 72 / 2;
                int centerY = 1 + 72 / 2;

                int diffX = context.getMouseX() - centerX;
                int diffY = context.getMouseY() - centerY;
                float radSq = diffY * diffY + diffX * diffX;
                if (radSq < 1296) {
                    focusedSubComponent = ClickedComponent.HUE;
                    updateOnMouseMovement(context);
                    return true;
                }

                if (valueSlider && context.translated(5 + 64 + 5, 5, 10, 64).isHovered()) {
                    focusedSubComponent = ClickedComponent.BRIGHTNESS;
                    updateOnMouseMovement(context);
                    return true;
                }
                if (opacitySlider && context.translated(5 + 64 + 5 + (valueSlider ? 15 : 0), 5, 10, 64).isHovered()) {
                    focusedSubComponent = ClickedComponent.OPACITY;
                    updateOnMouseMovement(context);
                    return true;
                }
                int chromaSpeed = ChromaColour.getSpeed(colour);
                int chromaX = 5 + 64 + 5 + (valueSlider ? 15 : 0) + (opacitySlider ? 15 : 0);
                if (chromaSpeed < 0) {
                    if (context.translated(chromaX, 5 + 27, 10, 10).isHovered()) {
                        colour = ChromaColour.special(200, c.getAlpha(), currentColour);
                        colourChangedCallback.accept(colour);
                    }
                } else {
                    if (context.translated(chromaX, 5, 10, 64).isHovered()) {
                        focusedSubComponent = ClickedComponent.CHROMA;
                        updateOnMouseMovement(context);
                    }
                }
            }
        }
        if (mouseEvent instanceof MouseEvent.Move && focusedSubComponent != null) {
            updateOnMouseMovement(context);
            return true;
        }

        if (mouseEvent instanceof MouseEvent.Scroll) {
            this.closeCallback.run();
            return true;
        }

        return componentHexField.mouseEvent(mouseEvent, context.translated(5 + 8, 5 + 64 + 5, 48, 12)) || context.isHovered();
    }

    @Override
    public boolean keyboardEvent(@NotNull KeyboardEvent event, @NotNull GuiImmediateContext context) {
        return componentHexField.keyboardEvent(event, context.translated(5 + 8, 5 + 64 + 5, 48, 10));
    }

    @Override
    public <T> T foldChildren(T initial, @NotNull BiFunction<@NotNull GuiComponent, T, T> visitor) {
        return visitor.apply(componentHexField, initial);
    }

}
