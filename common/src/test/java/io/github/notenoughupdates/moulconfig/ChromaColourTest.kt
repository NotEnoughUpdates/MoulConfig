package io.github.notenoughupdates.moulconfig

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.awt.Color

/**
 * Test to verfiy that [ChromaColour.toLegacyString] and [ChromaColour.forLegacyString] both work as expected, serializing the RGB value.
 */
class ChromaColourTest {
    fun testWithSpeed(jColor: Color, speed: Int) {
        val stringRepr = "$speed:${jColor.alpha}:${jColor.red}:${jColor.green}:${jColor.blue}"
        val time = if (speed > 0) (ChromaColour.getSecondsForSpeed(speed) * 1000).toInt() else 0
        val fromStatic = ChromaColour.fromRGB(jColor.red, jColor.green, jColor.blue, time, jColor.alpha)
        if (speed == 0) // Can't test colour accuracy given that the system time influences this
            Assertions.assertEquals(jColor.rgb, fromStatic.getEffectiveColourRGB())
        Assertions.assertEquals(stringRepr, fromStatic.toLegacyString())
        Assertions.assertEquals(fromStatic, ChromaColour.forLegacyString(stringRepr))
    }

    @Disabled("This test takes around ~8 seconds to run, we don't need that overhead")
    @Test
    fun testAllColours() {
        yieldAllColours()
            .forEach {
                testWithSpeed(it, 0)
                testWithSpeed(it, 10)
                testWithSpeed(it, 255)
            }
    }

    fun yieldAllColours() = (0..0xFFFFFF)
        .asSequence()
        .map { Color(it) }
}
