package io.github.notenoughupdates.moulconfig.internal;

public class MathUtil {
    public static int squaredDistance(int a, int b) {
        return (a - b) * (a - b);
    }

    public static int copySign(int signSource, int value) {
        if (signSource < 0) {
            return -Math.abs(value);
        }
        return Math.abs(value);
    }
}
