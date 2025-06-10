package moe.nea.shale.layout

enum class LayoutAxis {
    VERTICAL,
    HORIZONTAL,
    ;

    fun choose(position: Position) = when (this) {
        VERTICAL -> position.y
        HORIZONTAL -> position.x
    }

    fun unchoosePosition(scalar: Int) = when (this) {
        VERTICAL -> Position(0, scalar)
        HORIZONTAL -> Position(scalar, 0)
    }

    fun limit(size: Size) = when (this) {
        VERTICAL -> size.copy(width = 0)
        HORIZONTAL -> size.copy(height = 0)
    }

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

enum class LayoutOrientation {
    FORWARDS,
    BACKWARDS,
    ;

    fun multiply(scalar: Int) = when (this) {
        FORWARDS -> scalar
        BACKWARDS -> -scalar
    }

    fun compensateForOwnSize(scalar: Int) = when (this) {
        FORWARDS -> 0
        BACKWARDS -> -scalar
    }
}

enum class LayoutDirection {
    // TODO: this class could be split up into a vertical and a horizontal component with one of these each, as well as another attribute deciding which one is the major axis.
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
    ;

    fun chooseBeginningPosition(padding: Padding, size: Size): Position {
        return when (this) {
            LEFT_TO_RIGHT -> Position(padding.left, padding.top)
            RIGHT_TO_LEFT -> Position(size.width - padding.right, padding.top)
            TOP_TO_BOTTOM -> Position(padding.left, padding.top)
            BOTTOM_TO_TOP -> Position(padding.left, size.height - padding.bottom)
        }
    }

    val orientation: LayoutOrientation
        get() = when (this) {
            LEFT_TO_RIGHT -> LayoutOrientation.FORWARDS
            RIGHT_TO_LEFT -> LayoutOrientation.BACKWARDS
            TOP_TO_BOTTOM -> LayoutOrientation.FORWARDS
            BOTTOM_TO_TOP -> LayoutOrientation.BACKWARDS
        }

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
