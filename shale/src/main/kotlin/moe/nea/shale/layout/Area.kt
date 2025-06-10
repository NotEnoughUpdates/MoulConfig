package moe.nea.shale.layout

data class Area(val x: Int, val y: Int, val width: Int, val height: Int) {

    val left get() = x
    val top get() = y
    val right get() = x + width
    val bottom get() = y + height
    fun roughUnion(area: Area): Area {
        return fromExtents(
            left = minOf(left, area.left),
            top = minOf(top, area.top),
            right = maxOf(right, area.right),
            bottom = maxOf(bottom, area.bottom),
        )
    }

    companion object {
        val ZERO_AT_ORIGIN = Area(Position.ZERO, Size.ZERO)
        fun fromExtents(left: Int, top: Int, right: Int, bottom: Int) =
            Area(left, right, width = right - left, height = bottom - top)
    }


    constructor(position: Position, size: Size) : this(position.x, position.y,  size.width, size.height)

    val position: Position get() = Position(x, y)
    val size: Size get() = Size(width, height)
}
