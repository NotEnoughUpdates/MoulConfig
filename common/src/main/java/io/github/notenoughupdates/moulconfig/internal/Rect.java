package io.github.notenoughupdates.moulconfig.internal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Rect {
    int left, top, right, bottom;

    public static Rect ofDot(int x, int y) {
        return ofXYWH(x, y, 0, 0);
    }

    public static Rect ofLTRB(int left, int top, int right, int bottom) {
        return new Rect(left, top, right, bottom);
    }

    public static Rect ofXYWH(int x, int y, int w, int h) {
        return new Rect(x, y, x + w, y + h);
    }

    public int getX() {
        return left;
    }

    public int getY() {
        return top;
    }

    public int getW() {
        return right - left;
    }

    public int getH() {
        return bottom - top;
    }

    public Rect roughMerge(Rect other) {
        return new Rect(
            Math.min(left, other.left),
            Math.min(top, other.top),
            Math.max(right, other.right),
            Math.max(bottom, other.bottom)
        );
    }

    public Rect includePoint(int x, int y) {
        return new Rect(
            Math.min(left, x),
            Math.min(top, y),
            Math.max(right, x),
            Math.max(bottom, y)
        );
    }

    public Rect boundedBy(Rect other) {
        int l = Math.max(left, other.left);
        int t = Math.max(top, other.top);
        return new Rect(
            l,
            t,
            Math.max(l, Math.min(right, other.right)),
            Math.max(t, Math.min(bottom, other.bottom))
        );
    }
}
