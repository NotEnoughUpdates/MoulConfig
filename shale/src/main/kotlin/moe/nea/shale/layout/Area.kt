package moe.nea.shale.layout

data class Area(val x: Int, val y: Int, val width: Int, val height: Int) {
    constructor(position: Position, size: Size) : this(position.x, position.y, size.width, size.height)

    val position: Position get() = Position(x, y)
    val size: Size get() = Size(width, height)
}
