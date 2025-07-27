package io.github.notenoughupdates.moulconfig.common.text;

import io.github.notenoughupdates.moulconfig.internal.ColourUtil;
import io.github.notenoughupdates.moulconfig.internal.MathUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A list of (named) colours available on most backends
 */
public enum DefaultFormattingColour {
    BLACK(0x000000, 0),
    DARK_BLUE(0x0000aa, 1),
    DARK_GREEN(0x00aa00, 2),
    DARK_AQUA(0x00aaaa, 3),
    DARK_RED(0xaa0000, 4),
    DARK_PURPLE(0xaa00aa, 5),
    GOLD(0xffaa00, 6),
    GREY(0xaaaaaa, 7),
    DARK_GREY(0x555555, 8),
    BLUE(0x5555ff, 9),
    GREEN(0x55ff55, 10),
    AQUA(0x55ffff, 11),
    RED(0xff5555, 12),
    LIGHT_PURPLE(0xff55ff, 13),
    YELLOW(0xffff55, 14),
    WHITE(0xffffff, 15),
    ;

    @Getter
    final int rgb;
    @Getter
    final int colorHexDigit;

    DefaultFormattingColour(int rgb, int colorHexDigit) {
        this.rgb = rgb;
        this.colorHexDigit = colorHexDigit;
    }

    public static @NotNull DefaultFormattingColour estimate(int rgb) {
        int r = ColourUtil.unpackARGBRedI(rgb);
        int g = ColourUtil.unpackARGBGreenI(rgb);
        int b = ColourUtil.unpackARGBBlueI(rgb);
        int minDistance = Integer.MAX_VALUE;
        DefaultFormattingColour best = null;
        for (DefaultFormattingColour candidate : ALL_COLOURS) {
            int crgb = candidate.getRgb();
            int cr = ColourUtil.unpackARGBRedI(crgb);
            int cg = ColourUtil.unpackARGBGreenI(crgb);
            int cb = ColourUtil.unpackARGBBlueI(crgb);
            int sqDist =
                MathUtil.squaredDistance(cr, r)
                    + MathUtil.squaredDistance(cg, g)
                    + MathUtil.squaredDistance(cb, b);
            if (sqDist < minDistance)
                best = candidate;
        }
        assert best != null;
        return best;
    }

    public static final @Unmodifiable
    @NotNull List<@NotNull DefaultFormattingColour> ALL_COLOURS
        = Collections.unmodifiableList(Arrays.asList(values()));
}
