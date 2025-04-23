package io.github.notenoughupdates.moulconfig

import com.google.gson.annotations.Expose
import java.awt.Color
import kotlin.math.abs

@Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
data class ChromaColour(
    /**
     * Hue in a range from 0 to 1. For a chroma colour this is added to the time as an offset.
     */
    @Expose
    val hue: Float,
    /**
     * Saturation in a range from 0 to 1
     */
    @Expose
    val saturation: Float,
    /**
     * Brightness in a range from 0 to 1
     */
    @Expose
    val brightness: Float,
    /**
     * If set to 0, this indicates a static colour. If set to a value above 0, indicates the amount of milliseconds that pass until the same colour is met again.
     * This value may be saved lossy.
     */
    @Expose
    val timeForFullRotationInMillis: Int,
    /**
     * Alpha in a range from 0 to 255 (with 255 being fully opaque).
     */
    @Expose
    val alpha: Int,
) {

    private fun evaluateColourWithShift(hueShift: Double): Int {
        if (abs(cachedRGBHueOffset - hueShift) < 1/360.0)return cachedRGB
        val effectiveHue = ((hue.toDouble() + hueShift)%1).toFloat()
        val ret = (Color.HSBtoRGB(effectiveHue, saturation, brightness) and 0x00FFFFFF) or (alpha shl 24)
        cachedRGBHueOffset = hueShift
        cachedRGB = ret
        return ret
    }

    /**
     * The value of [evaluateColourWithShift] at [cachedRGBHueOffset]
     */
    @Transient
    private var cachedRGB: Int = 0

    /**
     * The last queried value of [evaluateColourWithShift].
     */
    @Transient
    private var cachedRGBHueOffset: Double = Double.NaN

    /**
     * @param offset offset the colour by a hue amount.
     * @return the colour, at the current time if this is a chrome colour
     */
    fun getEffectiveColourRGB(offset: Float): Int {
        var effectiveHueOffset = if (timeForFullRotationInMillis > 0) {
            System.currentTimeMillis() / timeForFullRotationInMillis.toDouble()
        } else {
            .0
        }
        effectiveHueOffset += offset
        return evaluateColourWithShift(effectiveHueOffset)
    }
    /**
     * @param offset offset the colour by a hue amount.
     * @return the colour, at the current time if this is a chrome colour
     */
    fun getEffectiveColour(offset: Float): Color = Color(getEffectiveColourRGB(offset), true)

    /**
     * Unlike [getEffectiveColourRGB], this offset does not change anything if not using an animated colour.
     *
     * @param offset offset the colour by a time amount in milliseconds.
     * @return the colour, at the current time if this is a chrome colour
     */
    fun getEffectiveColourWithTimeOffsetRGB(offset: Int): Int {
        if (timeForFullRotationInMillis == 0) return evaluateColourWithShift(.0)
        val effectiveHue = (System.currentTimeMillis() + offset) / timeForFullRotationInMillis.toDouble()
        return evaluateColourWithShift(effectiveHue)
    }

    /**
     * Unlike [getEffectiveColour], this offset does not change anything if not using an animated colour.
     *
     * @param offset offset the colour by a time amount in milliseconds.
     * @return the colour, at the current time if this is a chrome colour
     */
    fun getEffectiveColourWithTimeOffset(offset: Int): Color = Color(getEffectiveColourWithTimeOffsetRGB(offset), true)

    /**
     * @return the colour, at the current time if this is a chrome colour
     */
    fun getEffectiveColourRGB(): Int = getEffectiveColourWithTimeOffsetRGB(0)

    /**
     * @return the colour, at the current time if this is a chrome colour
     */
    fun getEffectiveColour(): Color = getEffectiveColourWithTimeOffset(0)

    @Deprecated("")
    fun toLegacyString(): String {
        val timeInSeconds = timeForFullRotationInMillis / 1000
        val namedSpeed =
        if (timeInSeconds == 0) 0 else (255 - (timeInSeconds - MIN_CHROMA_SECS) * 254f / (MAX_CHROMA_SECS - MIN_CHROMA_SECS)).toInt()
        val red = cachedRGB shr 16 and 0xFF
        val green = cachedRGB shr 8 and 0xFF
        val blue = cachedRGB and 0xFF
        return special(namedSpeed, alpha, red, green, blue)
    }

    companion object {

        @JvmStatic
        @Deprecated("")
        fun special(chromaSpeed: Int, alpha: Int, rgb: Int): String {
            return special(chromaSpeed, alpha, rgb shr 16 and 0xFF, rgb shr 8 and 0xFF, rgb and 0xFF)
        }

        private const val RADIX: Int = 10

        @JvmStatic
        @Deprecated("")
        fun special(chromaSpeed: Int, alpha: Int, r: Int, g: Int, b: Int): String {
            val sb = StringBuilder()
            sb.append(chromaSpeed.toString(RADIX)).append(":")
            sb.append(alpha.toString(RADIX)).append(":")
            sb.append(r.toString(RADIX)).append(":")
            sb.append(g.toString(RADIX)).append(":")
            sb.append(b.toString(RADIX))
            return sb.toString()
        }

        @JvmStatic
        private fun decompose(csv: String): IntArray {
            val split = csv.split(":")

            val arr = IntArray(split.size)

            for (i in split.indices) {
                try {
                    arr[i] = split[split.size - 1 - i].toInt(RADIX)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
            return arr
        }

        @JvmStatic
        @Deprecated("")
        fun specialToSimpleRGB(special: String): Int {
            val (b, g, r, a) = decompose(special)

            return (a and 0xFF) shl 24 or ((r and 0xFF) shl 16) or ((g and 0xFF) shl 8) or (b and 0xFF)
        }

        @JvmStatic
        @Deprecated("")
        fun getSpeed(special: String): Int = decompose(special)[4]

        private const val MIN_CHROMA_SECS: Int = 1
        private const val MAX_CHROMA_SECS: Int = 60

        @JvmStatic
        @Deprecated("")
        fun getSecondsForSpeed(speed: Int): Float = (255 - speed) / 254f * (MAX_CHROMA_SECS - MIN_CHROMA_SECS) + MIN_CHROMA_SECS

        @JvmStatic
        @Deprecated("")
        fun specialToChromaRGB(special: String): Int {
            val (b, g, r, a, chr) = decompose(special)

            val hsv = Color.RGBtoHSB(r, g, b, null)

            if (chr > 0) {
                val seconds = getSecondsForSpeed(chr)
                hsv[0] += (((System.currentTimeMillis().toDouble()) / 1000.0 / seconds) % 1).toFloat()
                hsv[0] %= 1f
                if (hsv[0] < 0) hsv[0] += 1f
            }

            return (a and 0xFF) shl 24 or (Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) and 0x00FFFFFF)
        }

        @JvmStatic
        @Deprecated("")
        fun rotateHue(argb: Int, degrees: Int): Int {
            val a = (argb shr 24) and 0xFF
            val r = (argb shr 16) and 0xFF
            val g = (argb shr 8) and 0xFF
            val b = (argb) and 0xFF

            val hsv = Color.RGBtoHSB(r, g, b, null)

            hsv[0] += degrees / 360f
            hsv[0] %= 1f

            return (a and 0xFF) shl 24 or (Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]) and 0x00FFFFFF)
        }

        @JvmStatic
        @Deprecated("")
        fun forLegacyString(stringRepresentation: String): ChromaColour {
            val d = decompose(stringRepresentation)
            assert(d.size == 5)

            val chr = d[4]
            val a = d[3]
            val r = d[2]
            val g = d[1]
            val b = d[0]
            return fromRGB(r, g, b, if (chr > 0) (getSecondsForSpeed(chr) * 1000).toInt() else 0, a)
        }

        @JvmStatic
        fun fromStaticRGB(r: Int, g: Int, b: Int, a: Int): ChromaColour = fromRGB(r, g, b, 0, a)

        @JvmStatic
        fun fromRGB(r: Int, g: Int, b: Int, chromaSpeedMillis: Int, a: Int): ChromaColour {
            val floats = Color.RGBtoHSB(r, g, b, null)
            return ChromaColour(floats[0], floats[1], floats[2], chromaSpeedMillis, a)
        }
    }
}
