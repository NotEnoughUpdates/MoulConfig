package moe.nea.shale.layout

data class Padding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
) {
    val sizes get() = Size(left + right, top + bottom)

    constructor(all: Int) : this(all, all, all, all)
    constructor(horizontal: Int, vertical: Int) : this(horizontal, vertical, horizontal, vertical)

    companion object {
        val NONE: Padding = Padding(all = 0)
    }

}
