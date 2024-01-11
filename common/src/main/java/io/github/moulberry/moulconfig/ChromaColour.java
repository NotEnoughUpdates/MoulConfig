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

package io.github.moulberry.moulconfig;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@Data
@AllArgsConstructor
public class ChromaColour {
    /**
     * Hue in a range from 0 to 1. For a chroma colour this is added to the time as an offset.
     */
    @Expose
    float hue;
    /**
     * Saturation in a range from 0 to 1
     */
    @Expose
    float saturation;
    /**
     * Brightness in a range from 0 to 1
     */
    @Expose
    float brightness;
    /**
     * If set to 0, this indicates a static colour. If set to a value above 0, indicates the amount of milliseconds that pass until the same colour is met again.
     * This value may be saved lossy.
     */
    @Expose
    int timeForFullRotationInMillis;
    /**
     * Alpha in a range from 0 to 255 (with 255 being fully opaque).
     */
    @Expose
    int alpha;

    /**
     * @param offset offset the colour by a hue amount.
     * @return the colour, at the current time if this is a chrome colour
     */
    public Color getEffectiveColour(float offset) {
        double effectiveHue;
        if (timeForFullRotationInMillis > 0) {
            effectiveHue = System.currentTimeMillis() / (double) timeForFullRotationInMillis;
        } else {
            effectiveHue = hue;
        }
        effectiveHue += offset;
        return new Color(Color.HSBtoRGB((float) (effectiveHue % 1), saturation, brightness) | (alpha << 24), true);
    }

    /**
     * Unlike {@link #getEffectiveColour(float)}, this offset does not change anything if not using an animated colour.
     *
     * @param offset offset the colour by a time amount in milliseconds.
     * @return the colour, at the current time if this is a chrome colour
     */
    public Color getEffectiveColourWithTimeOffset(int offset) {
        double effectiveHue;
        if (timeForFullRotationInMillis > 0) {
            effectiveHue = (System.currentTimeMillis() + offset) / (double) timeForFullRotationInMillis;
        } else {
            effectiveHue = hue;
        }
        return new Color(Color.HSBtoRGB((float) (effectiveHue % 1), saturation, brightness) | (alpha << 24), true);

    }


    /**
     * @return the colour, at the current time if this is a chrome colour
     */
    public Color getEffectiveColour() {
        return getEffectiveColour(0);
    }

    @Deprecated
    public String toLegacyString() {
        int timeInSeconds = timeForFullRotationInMillis / 1000;
        int namedSpeed =
            timeInSeconds == 0 ? 0 : (int) (255 - (timeInSeconds - MIN_CHROMA_SECS) * 254F / (MAX_CHROMA_SECS - MIN_CHROMA_SECS));
        return special(namedSpeed , alpha, Color.HSBtoRGB(hue, saturation, brightness));
    }

    @Deprecated
    public static String special(int chromaSpeed, int alpha, int rgb) {
        return special(chromaSpeed, alpha, (rgb & 0xFF0000) >> 16, (rgb & 0x00FF00) >> 8, (rgb & 0x0000FF));
    }

    private static final int RADIX = 10;

    @Deprecated
    public static String special(int chromaSpeed, int alpha, int r, int g, int b) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(chromaSpeed, RADIX)).append(":");
        sb.append(Integer.toString(alpha, RADIX)).append(":");
        sb.append(Integer.toString(r, RADIX)).append(":");
        sb.append(Integer.toString(g, RADIX)).append(":");
        sb.append(Integer.toString(b, RADIX));
        return sb.toString();
    }

    private static int[] decompose(String csv) {
        String[] split = csv.split(":");

        int[] arr = new int[split.length];

        for (int i = 0; i < split.length; i++) {
            try {
                arr[i] = Integer.parseInt(split[split.length - 1 - i], RADIX);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return arr;
    }

    @Deprecated
    public static int specialToSimpleRGB(String special) {
        int[] d = decompose(special);
        int r = d[2];
        int g = d[1];
        int b = d[0];
        int a = d[3];
        int chr = d[4];

        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    @Deprecated
    public static int getSpeed(String special) {
        return decompose(special)[4];
    }

    @Deprecated
    public static float getSecondsForSpeed(int speed) {
        return (255 - speed) / 254f * (MAX_CHROMA_SECS - MIN_CHROMA_SECS) + MIN_CHROMA_SECS;
    }

    private static final int MIN_CHROMA_SECS = 1;
    private static final int MAX_CHROMA_SECS = 60;


    @Deprecated
    public static int specialToChromaRGB(String special) {
        int[] d = decompose(special);
        int chr = d[4];
        int a = d[3];
        int r = d[2];
        int g = d[1];
        int b = d[0];

        float[] hsv = Color.RGBtoHSB(r, g, b, null);

        if (chr > 0) {
            float seconds = getSecondsForSpeed(chr);
            hsv[0] += (((double) System.currentTimeMillis()) / 1000.0 / seconds) % 1;
            hsv[0] %= 1;
            if (hsv[0] < 0) hsv[0] += 1;
        }

        return (a & 0xFF) << 24 | (Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) & 0x00FFFFFF);
    }

    @Deprecated
    public static int rotateHue(int argb, int degrees) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = (argb) & 0xFF;

        float[] hsv = Color.RGBtoHSB(r, g, b, null);

        hsv[0] += degrees / 360f;
        hsv[0] %= 1;

        return (a & 0xFF) << 24 | (Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) & 0x00FFFFFF);
    }

    @Deprecated
    public static ChromaColour forLegacyString(String stringRepresentation) {
        int[] d = decompose(stringRepresentation);
        assert d.length == 5;

        int chr = d[4];
        int a = d[3];
        int r = d[2];
        int g = d[1];
        int b = d[0];
        return fromRGB(r, g, b, chr > 0 ? (int) (getSecondsForSpeed(chr) * 1000) : 0, a);
    }

    public static ChromaColour fromStaticRGB(int r, int g, int b, int a) {
        return fromRGB(r, g, b, 0, a);
    }

    public static ChromaColour fromRGB(int r, int g, int b, int chromaSpeedMillis, int a) {
        float[] floats = Color.RGBtoHSB(r, g, b, null);
        return new ChromaColour(floats[0], floats[1], floats[2], chromaSpeedMillis, a);
    }
}
