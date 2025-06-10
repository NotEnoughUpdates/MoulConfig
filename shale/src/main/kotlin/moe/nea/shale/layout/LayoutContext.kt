package moe.nea.shale.layout

interface LayoutContext {
    fun measureWrappedText(text: String, width: Int): MeasuredText
    fun measureUnwrappedText(text: String): Int

    val textAxis: LayoutAxis
        get() = LayoutAxis.HORIZONTAL
}
