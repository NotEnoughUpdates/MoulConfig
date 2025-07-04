package io.github.notenoughupdates.moulconfig.internal;

public class ColourUtil {
    public static int packARGB(float r, float g, float b, float a) {
        int ri = float1ToInt255(r);
        int gi = float1ToInt255(g);
        int bi = float1ToInt255(b);
        int ai = float1ToInt255(a);
        return (ai << ALPHA_SHIFT) | (ri << RED_SHIFT) | (gi << GREEN_SHIFT) | (bi << BLUE_SHIFT);
    }

    public static float int255ToFloat1(int element) {
        return (element / COMPONENT_SIZE_F);
    }

    public static int float1ToInt255(float element) {
        return (int) (element * COMPONENT_SIZE_F);
    }

    public static int unpackARGBRedI(int color) {
        return (color >> RED_SHIFT) & BYTE_MASK;
    }

    public static int unpackARGBGreenI(int color) {
        return (color >> GREEN_SHIFT) & BYTE_MASK;
    }

    public static int unpackARGBBlueI(int color) {
        return (color >> BLUE_SHIFT) & BYTE_MASK;
    }

    public static int unpackARGBAlphaI(int color) {
        return (color >> ALPHA_SHIFT) & BYTE_MASK;
    }

    public static int makeOpaque(int argb) {
        return argb | MAX_ALPHA;
    }

    public static int makeTransparent(int argb) {
        return argb & ~MAX_ALPHA;
    }

    public static final float COMPONENT_SIZE_F = 255F;
    public static final int BYTE_MASK = 0xFF;
    public static final int ALPHA_SHIFT = 24;
    public static final int RED_SHIFT = 16;
    public static final int GREEN_SHIFT = 8;
    public static final int BLUE_SHIFT = 0;
    public static final int MAX_ALPHA = 0xFF000000;
}
