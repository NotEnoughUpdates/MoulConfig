package moe.nea.shale.layout

import kotlin.math.min

class BoxElement : Element() {


    override fun beforePass(layoutPass: LayoutPass) {
        super.beforePass(layoutPass)
        when (layoutPass) {
            is LayoutPass.Reset -> {
                preferredSize = Size.ZERO
            }

            is LayoutPass.Grow -> {
                if (direction.axis == layoutPass.axis) {
                    growChildElementsAlongAxis()
                } else {
                    val crossAxis = direction.axis.cross
                    val myHeight = crossAxis.choose(preferredSize) - crossAxis.choose(padding.sizes)
                    children.filter { it.sizing is Sizing.GrowFractional }
                        .forEach {
                            it.preferredSize = crossAxis.setUnchoosenSize(it.preferredSize, myHeight)
                        }
                }
            }

            else -> {}
        }
    }

    private fun growChildElementsAlongAxis() {
        // Roughly based on https://www.w3.org/TR/css-flexbox-1/#resolve-flexible-lengths
        // Consider also an algorithm that tries to rectify the inconsistencies of the initial size
        val axis = direction.axis
        fun remainingSize() = axis.choose(preferredSize) - axis.choose(padding.sizes) - ((children.size - 1) * childGap) - children.sumOf { axis.choose(it.preferredSize) }
        val growable = children.filter { it.sizing is Sizing.GrowFractional }
        while (growable.isNotEmpty()) {
            val remainingFreeSpace = remainingSize()
            if (remainingFreeSpace <= 0) break

            var smallest = axis.choose(growable.first().preferredSize)
            var secondSmallestSize = Int.MAX_VALUE
            var widthToAdd = remainingFreeSpace
            for (child in growable) {
                val childWidth = axis.choose(child.preferredSize)
                if (childWidth < smallest) {
                    secondSmallestSize = smallest
                    smallest = childWidth
                }
                if (childWidth > smallest) {
                    secondSmallestSize = minOf(secondSmallestSize, childWidth)
                    widthToAdd = secondSmallestSize - smallest
                }
            }

            widthToAdd = min(widthToAdd, remainingFreeSpace / growable.size)
            require(widthToAdd >= 0)
            if (widthToAdd == 0) break

            for (child in growable) {
                val childWidth = axis.choose(child.preferredSize)
                if (childWidth == smallest) { // TODO: this feels like i could have some degenerate cases with a ton of loops
                    child.preferredSize += axis.unchooseSize(widthToAdd)
                    // TODO: if the child element has a max size coerce to that max size and remove from growable list
                }
            }
        }
    }

    override fun afterPass(layoutPass: LayoutPass) {
        super.afterPass(layoutPass)
        when (layoutPass) {
            /**
             * n.b.: we do not care about [sizing] in the first [LayoutPass.Fit] pass. If we have a content size we want, we will take it, *afterwards* we can decide to grow or stay the same.
             */
            is LayoutPass.Fit -> {
                val axis = direction.axis
                val layoutAxis = layoutPass.axis
                preferredSize += layoutAxis.limit(padding.sizes)
                minimumSize += layoutAxis.limit(padding.sizes)
                if (axis == layoutPass.axis) {
                    preferredSize += axis.unchooseSize(
                        children.sumOf { axis.choose(it.preferredSize) }
                            + (children.size - 1) * childGap
                    )
                    minimumSize += axis.unchooseSize(
                        children.sumOf { axis.choose(it.minimumSize) }
                            + (children.size - 1) * childGap)
                } else {
                    preferredSize += axis.cross.unchooseSize(
                        children.maxOfOrNull { axis.cross.choose(it.preferredSize) } ?: 0
                    )
                    minimumSize += axis.cross.unchooseSize(
                        children.maxOfOrNull { axis.cross.choose(it.minimumSize) } ?: 0
                    )
                }
                // While not technically correct to size outside of the layout pass here, it doesnt matter since fixed sizing is not affected by any layouting pass anyway.
                (sizing as? Sizing.Fixed)?.let {
                    minimumSize = it.size + padding.sizes
                    preferredSize = it.size + padding.sizes
                }
                // TODO: respect maximum size
            }

            is LayoutPass.RelativePosition -> {
                val corner = direction.chooseBeginningPosition(padding, preferredSize)
                var pointer = corner
                val axis = direction.axis
                val orientation = direction.orientation
                children.forEach {
                    val childPreferredSizeAlongAxis = axis.choose(it.preferredSize)
                    it.relativePosition = pointer + axis.unchoosePosition(orientation.compensateForOwnSize(childPreferredSizeAlongAxis))
                    pointer += axis.unchoosePosition(orientation.multiply(childPreferredSizeAlongAxis + childGap))
                }
            }

            else -> {}
        }
    }
}
