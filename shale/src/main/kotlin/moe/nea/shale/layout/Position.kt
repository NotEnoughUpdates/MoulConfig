package moe.nea.shale.layout

data class Position(val x: Int, val y: Int) {
    companion object {
        val ZERO = Position(0, 0)
    }

    operator fun plus(pos: Position) = Position(x + pos.x, y + pos.y)
}
