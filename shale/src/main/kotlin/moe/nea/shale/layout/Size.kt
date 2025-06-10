package moe.nea.shale.layout

data class Size(val width: Int, val height: Int) {
    init {
        require(width >= 0){
            "width must be greater than or equal to 0"
        }
        require(height >= 0) {
            "height must be greater than or equal to 0"
        }
    }

    companion object {
        fun coerced(width: Int, height: Int): Size {
            return Size(width.coerceAtLeast(0), height.coerceAtLeast(0))
        }

        val ZERO = Size(0, 0)
    }

    operator fun plus(size: Size) = Size(width + size.width, height + size.height)
    operator fun minus(size: Size) = Size(width - size.width, height - size.height)
}
