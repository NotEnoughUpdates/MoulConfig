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
    BLACK(0x000000),
    DARK_BLUE(0x0000aa),
    DARK_GREEN(0x00aa00),
    DARK_AQUA(0x00aaaa),
    DARK_RED(0xaa0000),
    DARK_PURPLE(0xaa00aa),
    GOLD(0xffaa00),
    GREY(0xaaaaaa),
    DARK_GREY(0x555555),
    BLUE(0x5555ff),
    GREEN(0x55ff55),
    AQUA(0x55ffff),
    RED(0xff5555),
    LIGHT_PURPLE(0xff55ff),
    YELLOW(0xffff55),
    WHITE(0xffffff),
    ;

    @Getter
    final int rgb;

    DefaultFormattingColour(int rgb) {
        this.rgb = rgb;
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
