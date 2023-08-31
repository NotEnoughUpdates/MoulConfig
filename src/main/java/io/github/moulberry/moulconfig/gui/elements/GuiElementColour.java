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

package io.github.moulberry.moulconfig.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.moulberry.moulconfig.ChromaColour;
import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.gui.GuiElement;
import io.github.moulberry.moulconfig.internal.RenderUtils;
import io.github.moulberry.moulconfig.internal.TextRenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class GuiElementColour extends GuiElement {
    private final GuiElementTextField hexField = new GuiElementTextField(
            "",
            GuiElementTextField.SCALE_TEXT | GuiElementTextField.FORCE_CAPS | GuiElementTextField.NO_SPACE
    );

    private final int x;
    private final int y;
    private int xSize = 119;
    private final int ySize = 89;

    private float wheelAngle = 0;
    private float wheelRadius = 0;

    private int clickedComponent = -1;

    private final Consumer<String> colourChangedCallback;
    private final Runnable closeCallback;
    private String colour;

    private final boolean opacitySlider;
    private final boolean valueSlider;

    public GuiElementColour(
            int x, int y, String initialColour, Consumer<String> colourChangedCallback,
            Runnable closeCallback
    ) {
        this(x, y, initialColour, colourChangedCallback, closeCallback, true, true);
    }

    public GuiElementColour(
            int x, int y, String initialColour, Consumer<String> colourChangedCallback,
            Runnable closeCallback, boolean opacitySlider, boolean valueSlider
    ) {
        this.y = Math.max(10, Math.min(MinecraftClient.getInstance().getWindow().getScaledHeight() - ySize - 10, y));
        this.x = Math.max(10, Math.min(MinecraftClient.getInstance().getWindow().getScaledWidth() - xSize - 10, x));

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

    long lastDump = System.currentTimeMillis();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawFloatingRectDark(context, x, y, xSize, ySize);

        int currentColour = ChromaColour.specialToSimpleRGB(colour);
        Color c = new Color(currentColour, true);
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
//
//        BufferedImage bufferedImage = new BufferedImage(288, 288, BufferedImage.TYPE_INT_ARGB);
//        float borderRadius = 0.05f;
//        //if (Keyboard.isKeyDown(Keyboard.KEY_N)) borderRadius = 0; //TODO find out if possible
//        for (int x = -16; x < 272; x++) {
//            for (int y = -16; y < 272; y++) {
//                float radius = (float) Math.sqrt(((x - 128) * (x - 128) + (y - 128) * (y - 128)) / 16384f);
//                float angle = (float) Math.toDegrees(Math.atan((128 - x) / (y - 128 + 1E-5)) + Math.PI / 2);
//                if (y < 128) angle += 180;
//                if (radius <= 1) {
//                    int rgb = Color.getHSBColor(angle / 360f, (float) Math.pow(radius, 1.5f), hsv[2]).getRGB();
//                    bufferedImage.setRGB(x + 16, y + 16, rgb);
//                } else if (radius <= 1 + borderRadius) {
//                    float invBlackAlpha = Math.abs(radius - 1 - borderRadius / 2) / borderRadius * 2;
//                    float blackAlpha = 1 - invBlackAlpha;
//
//                    if (radius > 1 + borderRadius / 2) {
//                        bufferedImage.setRGB(x + 16, y + 16, (int) (blackAlpha * 255) << 24);
//                    } else {
//                        Color col = Color.getHSBColor(angle / 360f, 1, hsv[2]);
//                        int rgb = (int) (col.getRed() * invBlackAlpha) << 16 |
//                                (int) (col.getGreen() * invBlackAlpha) << 8 |
//                                (int) (col.getBlue() * invBlackAlpha);
//                        bufferedImage.setRGB(x + 16, y + 16, 0xff000000 | rgb);
//                    }
//
//                }
//            }
//        }
//
//        BufferedImage bufferedImageValue = new BufferedImage(10, 64, BufferedImage.TYPE_INT_ARGB);
//        for (int x = 0; x < 10; x++) {
//            for (int y = 0; y < 64; y++) {
//                if ((x == 0 || x == 9) && (y == 0 || y == 63)) continue;
//
//                int rgb = Color.getHSBColor(wheelAngle / 360, wheelRadius, (64 - y) / 64f).getRGB();
//                bufferedImageValue.setRGB(x, y, rgb);
//            }
//        }
//
//        BufferedImage bufferedImageOpacity = new BufferedImage(10, 64, BufferedImage.TYPE_INT_ARGB);
//        for (int x = 0; x < 10; x++) {
//            for (int y = 0; y < 64; y++) {
//                if ((x == 0 || x == 9) && (y == 0 || y == 63)) continue;
//
//                int rgb = (currentColour & 0x00FFFFFF) | (Math.min(255, (64 - y) * 4) << 24);
//                bufferedImageOpacity.setRGB(x, y, rgb);
//            }
//        }

        float selradius = (float) Math.pow(wheelRadius, 1 / 1.5f) * 32;
        int selx = (int) (Math.cos(Math.toRadians(wheelAngle)) * selradius);
        int sely = (int) (Math.sin(Math.toRadians(wheelAngle)) * selradius);

        int valueOffset = 0;
        if (valueSlider) {
            valueOffset = 15;

            context.setShaderColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);
            context.drawTexture(GuiTextures.COLOUR_PICKER_VALUE, x + 5 + 64 + 5, y + 5, 0, 0, 10, 64, 10, 64);
            context.setShaderColor(1, 1, 1, 1);
        }

        int opacityOffset = 0;
        if (opacitySlider) {
            opacityOffset = 15;

            context.drawTexture(GuiTextures.COLOUR_SELECTOR_BAR_ALPHA, x + 5 + 64 + 5 + valueOffset, y + 5, 0, 0, 10, 64, 10, 64);


            context.setShaderColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);
            context.fillGradient( x + 5 + 64 + 5 + valueOffset, y + 7, x + 5 + 64 + 5 + valueOffset + 10, y + 69, 0xFFFFFFFF, 0x00FFFFFF);
            context.fill( x + 5 + 64 + 5 + 2 + valueOffset, y + 5, x + 5 + 64 + 5 + valueOffset + 8, y + 7, 0xFFFFFFFF);
            //context.drawTexture(GuiTextures.COLOUR_PICKER_OPACITY, x + 5 + 64 + 5 + valueOffset, y + 5, 0, 0, 10, 64, 10, 64);
            context.setShaderColor(1, 1, 1, 1);
        }

        int chromaSpeed = ChromaColour.getSpeed(colour);
        int currentColourChroma = ChromaColour.specialToChromaRGB(colour);
        Color cChroma = new Color(currentColourChroma, true);
        float[] hsvChroma = Color.RGBtoHSB(cChroma.getRed(), cChroma.getGreen(), cChroma.getBlue(), null);

        if (chromaSpeed > 0) {
            context.fill(x + 5 + 64 + valueOffset + opacityOffset + 5 + 1, y + 5 + 1,
                    x + 5 + 64 + valueOffset + opacityOffset + 5 + 10 - 1, y + 5 + 64 - 1,
                    Color.HSBtoRGB(hsvChroma[0], 0.8f, 0.8f)
            );
        } else {
            context.fill(x + 5 + 64 + valueOffset + opacityOffset + 5 + 1, y + 5 + 27 + 1,
                    x + 5 + 64 + valueOffset + opacityOffset + 5 + 10 - 1, y + 5 + 37 - 1,
                    Color.HSBtoRGB((float) (((double) hsvChroma[0] + (double) System.currentTimeMillis() / 1000.0) % 1), 0.8f, 0.8f)
            );
        }
        if (valueSlider)
            context.drawTexture(GuiTextures.COLOUR_SELECTOR_BAR, x + 5 + 64 + 5, y + 5, 0, 0, 10, 64, 10, 64);
        if (opacitySlider)
            context.drawTexture(GuiTextures.COLOUR_SELECTOR_BAR, x + 5 + 64 + 5 + valueOffset, y + 5, 0, 0, 10, 64, 10, 64);

        if (chromaSpeed > 0) {
            context.drawTexture(GuiTextures.COLOUR_SELECTOR_BAR, x + 5 + 64 + valueOffset + opacityOffset + 5, y + 5, 0, 0, 10, 64, 10, 64);
        } else {
            context.drawTexture(GuiTextures.COLOUR_SELECTOR_CHROMA, x + 5 + 64 + valueOffset + opacityOffset + 5, y + 5 + 27, 0, 0, 10, 10, 10, 10);
        }

        if (valueSlider) context.fill(x + 5 + 64 + 5, y + 5 + 64 - (int) (64 * hsv[2]),
                x + 5 + 64 + valueOffset, y + 5 + 64 - (int) (64 * hsv[2]) + 1, 0xFF000000
        );
        if (opacitySlider) context.fill(x + 5 + 64 + 5 + valueOffset, y + 5 + 64 - c.getAlpha() / 4,
                x + 5 + 64 + valueOffset + opacityOffset, y + 5 + 64 - c.getAlpha() / 4 - 1, 0xFF000000
        );
        if (chromaSpeed > 0) {
            context.fill(x + 5 + 64 + valueOffset + opacityOffset + 5,
                    y + 5 + 64 - (int) (chromaSpeed / 255f * 64),
                    x + 5 + 64 + valueOffset + opacityOffset + 5 + 10,
                    y + 5 + 64 - (int) (chromaSpeed / 255f * 64) + 1, 0xFF000000
            );
        }


        float colorScale =  Math.round(hsv[2] * 100) / 100f;

        context.setShaderColor(colorScale, colorScale, colorScale, 1);
        context.drawTexture(GuiTextures.COLOUR_PICKER, x + 1, y + 1, 0, 0, 72, 72, 72, 72);
        context.setShaderColor(1,1,1,1);

        context.drawTexture(GuiTextures.COLOUR_SELECTOR_DOT, x + 5 + 32 + selx - 4, y + 5 + 32 + sely - 4, 0, 0, 8, 8, 8, 8);

        TextRenderUtils.drawStringCenteredScaledMaxWidth(Formatting.GRAY.toString() + Math.round(hsv[2] * 100),
                context,
                x + 5 + 64 + 5 + 5 - (Math.round(hsv[2] * 100) == 100 ? 1 : 0), y + 5 + 64 + 5 + 5, true, 13, -1
        );
        if (opacitySlider) {
            TextRenderUtils.drawStringCenteredScaledMaxWidth(
                    Formatting.GRAY.toString() + Math.round(c.getAlpha() / 255f * 100),
                    context,
                    x + 5 + 64 + 5 + valueOffset + 5,
                    y + 5 + 64 + 5 + 5,
                    true,
                    13,
                    -1
            );
        }
        if (chromaSpeed > 0) {
            TextRenderUtils.drawStringCenteredScaledMaxWidth(Formatting.GRAY.toString() +
                            (int) ChromaColour.getSecondsForSpeed(chromaSpeed) + "s",
                    context,
                    x + 5 + 64 + 5 + valueOffset + opacityOffset + 6, y + 5 + 64 + 5 + 5, true, 13, -1
            );
        }

        hexField.setSize(48, 10);
        if (!hexField.getFocus()) hexField.setText(Integer.toHexString(c.getRGB() & 0xFFFFFF).toUpperCase());

        StringBuilder sb = new StringBuilder(Formatting.GRAY + "#");
        for (int i = 0; i < 6 - hexField.getText().length(); i++) {
            sb.append("0");
        }
        sb.append(Formatting.WHITE);

        hexField.setPrependText(sb.toString());
        hexField.render(context, x + 5 + 8, y + 5 + 64 + 5);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            clickedComponent = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return handleMouseClickAndDrag(mouseX, mouseY, button);
    }

    public boolean handleMouseClickAndDrag(double mouseX, double mouseY, int button) {
        if (button == 0 && clickedComponent >= 0) {
            int currentColour = ChromaColour.specialToSimpleRGB(colour);
            Color c = new Color(currentColour, true);
            float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

            float xWheel = (float) (mouseX - x - 5);
            float yWheel = (float) (mouseY - y - 5);

            if (clickedComponent == 0) {
                float angle = (float) Math.toDegrees(Math.atan((32 - xWheel) / (yWheel - 32 + 1E-5)) + Math.PI / 2);
                xWheel = Math.max(0, Math.min(64, xWheel));
                yWheel = Math.max(0, Math.min(64, yWheel));
                float radius = (float) Math.sqrt(((xWheel - 32) * (xWheel - 32) + (yWheel - 32) * (yWheel - 32)) / 1024f);
                if (yWheel < 32) angle += 180;

                this.wheelAngle = angle;
                this.wheelRadius = (float) Math.pow(Math.min(1, radius), 1.5f);
                int rgb = Color.getHSBColor(angle / 360f, wheelRadius, hsv[2]).getRGB();
                colour = ChromaColour.special(ChromaColour.getSpeed(colour), c.getAlpha(), rgb);
                colourChangedCallback.accept(colour);
                return true;
            }

            float y = (float) (mouseY - this.y - 5);
            y = Math.max(0, Math.min(64, y));

            if (clickedComponent == 1) {
                int rgb = Color.getHSBColor(wheelAngle / 360, wheelRadius, 1 - y / 64f).getRGB();
                colour = ChromaColour.special(ChromaColour.getSpeed(colour), c.getAlpha(), rgb);
                colourChangedCallback.accept(colour);
                return true;
            }

            if (clickedComponent == 2) {
                colour = ChromaColour.special(ChromaColour.getSpeed(colour),
                        255 - Math.round(y / 64f * 255), currentColour
                );
                colourChangedCallback.accept(colour);
                return true;
            }

            if (clickedComponent == 3) {
                colour = ChromaColour.special(255 - Math.round(y / 64f * 255), c.getAlpha(), currentColour);
                colourChangedCallback.accept(colour);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((button == 0 || button == 1)) {
            if (mouseX > x + 5 + 8 && mouseX < x + 5 + 8 + 48) {
                if (mouseY > y + 5 + 64 + 5 && mouseY < y + 5 + 64 + 5 + 10) {
                    hexField.mouseClicked(mouseX, mouseY, button);
                    clickedComponent = -1;
                    return true;
                }
            }
        }
        if (button == 0) {
            if (mouseX >= x && mouseX <= x + 119 &&
                    mouseY >= y && mouseY <= y + 89) {
                hexField.unfocus();

                int xWheel = (int) (mouseX - x - 5);
                int yWheel = (int) (mouseY - y - 5);

                if (xWheel > 0 && xWheel < 64) {
                    if (yWheel > 0 && yWheel < 64) {
                        clickedComponent = 0;
                    }
                }

                int xValue = (int) (mouseX - (x + 5 + 64 + 5));
                int y = (int) (mouseY - this.y - 5);

                int opacityOffset = opacitySlider ? 15 : 0;
                int valueOffset = valueSlider ? 15 : 0;

                if (y > -5 && y <= 69) {
                    if (valueSlider) {
                        if (xValue > 0 && xValue < 10) {
                            clickedComponent = 1;
                        }
                    }

                    if (opacitySlider) {
                        int xOpacity = (int) (mouseX - (x + 5 + 64 + 5 + valueOffset));

                        if (xOpacity > 0 && xOpacity < 10) {
                            clickedComponent = 2;
                        }
                    }
                }

                int chromaSpeed = ChromaColour.getSpeed(colour);
                int xChroma = (int) (mouseX - (x + 5 + 64 + valueOffset + opacityOffset + 5));
                if (xChroma > 0 && xChroma < 10) {
                    if (chromaSpeed > 0) {
                        if (y > -5 && y <= 69) {
                            clickedComponent = 3;
                        }
                    } else if (mouseY > this.y + 5 + 27 && mouseY < this.y + 5 + 37) {
                        int currentColour = ChromaColour.specialToSimpleRGB(colour);
                        Color c = new Color(currentColour, true);
                        colour = ChromaColour.special(200, c.getAlpha(), currentColour);
                        colourChangedCallback.accept(colour);
                    }
                }
            } else {
                hexField.unfocus();
                closeCallback.run();
                return false;
            }
        }
        return handleMouseClickAndDrag(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (hexField.getFocus()) {
            if (keyCode == InputUtil.GLFW_KEY_ESCAPE) {
                hexField.unfocus();
                return true;
            }
            String old = hexField.getText();

            hexField.keyTyped(keyCode, scanCode, modifiers);

            if (hexField.getText().length() > 6) {
                hexField.setText(old);
            } else {
                try {
                    String text = hexField.getText().toLowerCase();

                    int rgb = Integer.parseInt(text, 16);
                    int alpha = (ChromaColour.specialToSimpleRGB(colour) >> 24) & 0xFF;
                    colour = ChromaColour.special(ChromaColour.getSpeed(colour), alpha, rgb);
                    colourChangedCallback.accept(colour);

                    Color c = new Color(rgb);
                    float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                    updateAngleAndRadius(hsv);
                } catch (Exception ignored) {
                }
            }

            return true;
        }
        return false;
    }
}
