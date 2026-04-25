package io.github.notenoughupdates.moulconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.Color;

public class ChromaColourTest {
    void testWithSpeed(Color color, int speed) {
        String stringRepr = speed + ":" + color.getAlpha() + ":" + color.getRed() + ":" + color.getGreen() + ":" + color.getBlue();
        int time = speed > 0 ? (int) (ChromaColour.getSecondsForSpeed(speed) * 1000) : 0;
        ChromaColour fromStatic = ChromaColour.fromRGB(color.getRed(), color.getGreen(), color.getBlue(), time, color.getAlpha());
        if (speed == 0) {
            Assertions.assertEquals(color.getRGB(), fromStatic.getEffectiveColourRGB());
        }
        Assertions.assertEquals(stringRepr, fromStatic.toLegacyString());
        Assertions.assertEquals(fromStatic, ChromaColour.forLegacyString(stringRepr));
    }

    @Disabled("This test takes around ~8 seconds to run, we don't need that overhead")
    @Test
    void testAllColours() {
        for (int i = 0; i <= 0xFFFFFF; i++) {
            Color color = new Color(i);
            testWithSpeed(color, 0);
            testWithSpeed(color, 10);
            testWithSpeed(color, 255);
        }
    }
}
