package io.github.notenoughupdates.moulconfig;

import com.google.gson.annotations.Expose;

import java.awt.Color;
import java.util.Objects;

@SuppressWarnings({"DeprecatedIsStillUsed", "deprecation"})
public final class ChromaColour {
    @Expose
    private final float hue;
    @Expose
    private final float saturation;
    @Expose
    private final float brightness;
    @Expose
    private final int timeForFullRotationInMillis;
    @Expose
    private final int alpha;

    /**
     * The value of {@link #evaluateColourWithShift(double)} at {@link #cachedRGBHueOffset}.
     */
    private transient int cachedRGB;

    /**
     * The last queried value of {@link #evaluateColourWithShift(double)}.
     */
    private transient double cachedRGBHueOffset = Double.NaN;

    public ChromaColour(float hue, float saturation, float brightness, int timeForFullRotationInMillis, int alpha) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.timeForFullRotationInMillis = timeForFullRotationInMillis;
        this.alpha = alpha;
    }

    /**
     * Hue in a range from 0 to 1. For a chroma colour this is added to the time as an offset.
     */
    public float getHue() {
        return hue;
    }

    /**
     * Saturation in a range from 0 to 1.
     */
    public float getSaturation() {
        return saturation;
    }

    /**
     * Brightness in a range from 0 to 1.
     */
    public float getBrightness() {
        return brightness;
    }

    /**
     * If set to 0, this indicates a static colour. If set to a value above 0, indicates the amount of milliseconds that pass until the same colour is met again.
     * This value may be saved lossy.
     */
    public int getTimeForFullRotationInMillis() {
        return timeForFullRotationInMillis;
    }

    /**
     * Alpha in a range from 0 to 255 (with 255 being fully opaque).
     */
    public int getAlpha() {
        return alpha;
    }

    private int evaluateColourWithShift(double hueShift) {
        if (Math.abs(cachedRGBHueOffset - hueShift) < 1 / 360.0) {
            return cachedRGB;
        }
        float effectiveHue = (float) ((hue + hueShift) % 1);
        int ret = (Color.HSBtoRGB(effectiveHue, saturation, brightness) & 0x00FFFFFF) | (alpha << 24);
        cachedRGBHueOffset = hueShift;
        cachedRGB = ret;
        return ret;
    }

    /**
     * @param offset offset the colour by a hue amount.
     * @return the colour, at the current time if this is a chrome colour
     */
    public int getEffectiveColourRGB(float offset) {
        double effectiveHueOffset = timeForFullRotationInMillis > 0
            ? System.currentTimeMillis() / (double) timeForFullRotationInMillis
            : 0.0;
        effectiveHueOffset += offset;
        return evaluateColourWithShift(effectiveHueOffset);
    }

    /**
     * @param offset offset the colour by a hue amount.
     * @return the colour, at the current time if this is a chrome colour
     */
    public Color getEffectiveColour(float offset) {
        return new Color(getEffectiveColourRGB(offset), true);
    }

    /**
     * Unlike {@link #getEffectiveColourRGB(float)}, this offset does not change anything if not using an animated colour.
     *
     * @param offset offset the colour by a time amount in milliseconds.
     * @return the colour, at the current time if this is a chrome colour
     */
    public int getEffectiveColourWithTimeOffsetRGB(int offset) {
        if (timeForFullRotationInMillis == 0) {
            return evaluateColourWithShift(0.0);
        }
        double effectiveHue = (System.currentTimeMillis() + offset) / (double) timeForFullRotationInMillis;
        return evaluateColourWithShift(effectiveHue);
    }

    /**
     * Unlike {@link #getEffectiveColour(float)}, this offset does not change anything if not using an animated colour.
     *
     * @param offset offset the colour by a time amount in milliseconds.
     * @return the colour, at the current time if this is a chrome colour
     */
    public Color getEffectiveColourWithTimeOffset(int offset) {
        return new Color(getEffectiveColourWithTimeOffsetRGB(offset), true);
    }

    /**
     * @return the colour, at the current time if this is a chrome colour
     */
    public int getEffectiveColourRGB() {
        return getEffectiveColourWithTimeOffsetRGB(0);
    }

    /**
     * @return the colour, at the current time if this is a chrome colour
     */
    public Color getEffectiveColour() {
        return getEffectiveColourWithTimeOffset(0);
    }

    @Deprecated
    public String toLegacyString() {
        int namedSpeed = timeForFullRotationInMillis == 0 ? 0 : getSpeedForMillis(timeForFullRotationInMillis / 1000F);
        int rgb = evaluateColourWithShift(0.0);
        int red = rgb >> 16 & 0xFF;
        int green = rgb >> 8 & 0xFF;
        int blue = rgb & 0xFF;
        return special(namedSpeed, alpha, red, green, blue);
    }

    @Deprecated
    public static String special(int chromaSpeed, int alpha, int rgb) {
        return special(chromaSpeed, alpha, rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF);
    }

    private static final int RADIX = 10;

    @Deprecated
    public static String special(int chromaSpeed, int alpha, int r, int g, int b) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(chromaSpeed, RADIX)).append(':');
        sb.append(Integer.toString(alpha, RADIX)).append(':');
        sb.append(Integer.toString(r, RADIX)).append(':');
        sb.append(Integer.toString(g, RADIX)).append(':');
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
        int b = d[0];
        int g = d[1];
        int r = d[2];
        int a = d[3];
        return (a & 0xFF) << 24 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    @Deprecated
    public static int getSpeed(String special) {
        return decompose(special)[4];
    }

    private static final int MIN_CHROMA_SECS = 1;
    private static final int MAX_CHROMA_SECS = 60;

    @Deprecated
    public static float getSecondsForSpeed(int speed) {
        return (255 - speed) / 254F * (MAX_CHROMA_SECS - MIN_CHROMA_SECS) + MIN_CHROMA_SECS;
    }

    @Deprecated
    public static int getSpeedForMillis(float seconds) {
        return Math.round(255 - ((seconds - MIN_CHROMA_SECS) / (MAX_CHROMA_SECS - MIN_CHROMA_SECS) * 254));
    }

    @Deprecated
    public static int specialToChromaRGB(String special) {
        int[] d = decompose(special);
        int b = d[0];
        int g = d[1];
        int r = d[2];
        int a = d[3];
        int chr = d[4];
        float[] hsv = Color.RGBtoHSB(r, g, b, null);
        if (chr > 0) {
            float seconds = getSecondsForSpeed(chr);
            hsv[0] += (float) ((System.currentTimeMillis() / 1000.0 / seconds) % 1);
            hsv[0] %= 1F;
            if (hsv[0] < 0) {
                hsv[0] += 1F;
            }
        }
        return (a & 0xFF) << 24 | (Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) & 0x00FFFFFF);
    }

    @Deprecated
    public static int rotateHue(int argb, int degrees) {
        int a = argb >> 24 & 0xFF;
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        float[] hsv = Color.RGBtoHSB(r, g, b, null);
        hsv[0] += degrees / 360F;
        hsv[0] %= 1F;
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

    public ChromaColour copy(float hue, float saturation, float brightness, int timeForFullRotationInMillis, int alpha) {
        return new ChromaColour(hue, saturation, brightness, timeForFullRotationInMillis, alpha);
    }

    public float component1() { return hue; }
    public float component2() { return saturation; }
    public float component3() { return brightness; }
    public int component4() { return timeForFullRotationInMillis; }
    public int component5() { return alpha; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChromaColour)) return false;
        ChromaColour that = (ChromaColour) o;
        return Float.compare(that.hue, hue) == 0
            && Float.compare(that.saturation, saturation) == 0
            && Float.compare(that.brightness, brightness) == 0
            && timeForFullRotationInMillis == that.timeForFullRotationInMillis
            && alpha == that.alpha;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hue, saturation, brightness, timeForFullRotationInMillis, alpha);
    }

    @Override
    public String toString() {
        return "ChromaColour(hue=" + hue
            + ", saturation=" + saturation
            + ", brightness=" + brightness
            + ", timeForFullRotationInMillis=" + timeForFullRotationInMillis
            + ", alpha=" + alpha + ")";
    }
}
