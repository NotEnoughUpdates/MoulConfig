package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.ChromaColour;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Render {

    public static void main(String[] args) throws IOException {
        int currentColour = 0xFFFFFFFF;
        float[] hsv = Color.RGBtoHSB(0xFF, 0xFF, 0xFF, null);

        BufferedImage bufferedImage = new BufferedImage(288, 288, BufferedImage.TYPE_INT_ARGB);
        float borderRadius = 0.05f;
        //if (Keyboard.isKeyDown(Keyboard.KEY_N)) borderRadius = 0; //TODO find out if possible
        for (int x = -16; x < 272; x++) {
            for (int y = -16; y < 272; y++) {
                float radius = (float) Math.sqrt(((x - 128) * (x - 128) + (y - 128) * (y - 128)) / 16384f);
                float angle = (float) Math.toDegrees(Math.atan((128 - x) / (y - 128 + 1E-5)) + Math.PI / 2);
                if (y < 128) angle += 180;
                if (radius <= 1) {
                    int rgb = Color.getHSBColor(angle / 360f, (float) Math.pow(radius, 1.5f), hsv[2]).getRGB();
                    bufferedImage.setRGB(x + 16, y + 16, rgb);
                } else if (radius <= 1 + borderRadius) {
                    float invBlackAlpha = Math.abs(radius - 1 - borderRadius / 2) / borderRadius * 2;
                    float blackAlpha = 1 - invBlackAlpha;

                    if (radius > 1 + borderRadius / 2) {
                        bufferedImage.setRGB(x + 16, y + 16, (int) (blackAlpha * 255) << 24);
                    } else {
                        Color col = Color.getHSBColor(angle / 360f, 1, hsv[2]);
                        int rgb = (int) (col.getRed() * invBlackAlpha) << 16 |
                                (int) (col.getGreen() * invBlackAlpha) << 8 |
                                (int) (col.getBlue() * invBlackAlpha);
                        bufferedImage.setRGB(x + 16, y + 16, 0xff000000 | rgb);
                    }

                }
            }
        }


        float wheelRadius = hsv[1];
        float wheelAngle = hsv[0] * 360;

        BufferedImage bufferedImageValue = new BufferedImage(10, 64, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 64; y++) {
                if ((x == 0 || x == 9) && (y == 0 || y == 63)) continue;

                int rgb = Color.getHSBColor(wheelAngle / 360, wheelRadius, (64 - y) / 64f).getRGB();
                bufferedImageValue.setRGB(x, y, rgb);
            }
        }

        BufferedImage bufferedImageOpacity = new BufferedImage(10, 64, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 64; y++) {
                if ((x == 0 || x == 9) && (y == 0 || y == 63)) continue;

                int rgb = (currentColour & 0x00FFFFFF) | (Math.min(255, (64 - y) * 4) << 24);
                bufferedImageOpacity.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(bufferedImageOpacity, "png", Files.newOutputStream(Path.of("opacity.png"), StandardOpenOption.CREATE));
        ImageIO.write(bufferedImageValue, "png", Files.newOutputStream(Path.of("value.png"), StandardOpenOption.CREATE));
        ImageIO.write(bufferedImage, "png", Files.newOutputStream(Path.of("colorpicker.png"), StandardOpenOption.CREATE));
    }

}
