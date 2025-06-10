package moe.nea.shale.layout

sealed interface Sizing {
    object Fit : Sizing
    data class GrowFractional(val factor: Float) : Sizing {
        init {
            require(factor > 0)
        }
    }

    data class Fixed(val size: Size) : Sizing
}
