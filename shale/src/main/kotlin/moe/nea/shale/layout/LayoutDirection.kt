package moe.nea.shale.layout

enum class LayoutAxis {
    VERTICAL,
    HORIZONTAL,
    ;

    fun choose(size: Size) = when (this) {
        VERTICAL -> size.height
        HORIZONTAL -> size.width
    }

    fun unchooseSize(size: Int) = when (this) {
        VERTICAL -> Size(0, size)
        HORIZONTAL -> Size(size, 0)
    }

    fun setUnchoosenSize(size: Size, newElement: Int) = when (this) {
        VERTICAL -> size.copy(height = newElement)
        HORIZONTAL -> size.copy(width = newElement)
    }

    val cross
        get() = when (this) {
            VERTICAL -> HORIZONTAL
            HORIZONTAL -> VERTICAL
        }

}

enum class LayoutDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
    ;

    val axis
        get() = when (this) {
            RIGHT_TO_LEFT, LEFT_TO_RIGHT -> LayoutAxis.HORIZONTAL
            TOP_TO_BOTTOM, BOTTOM_TO_TOP -> LayoutAxis.VERTICAL
        }

    val inverse
        get() = when (this) {
            LEFT_TO_RIGHT -> RIGHT_TO_LEFT
            RIGHT_TO_LEFT -> LEFT_TO_RIGHT
            TOP_TO_BOTTOM -> BOTTOM_TO_TOP
            BOTTOM_TO_TOP -> TOP_TO_BOTTOM
        }
}
