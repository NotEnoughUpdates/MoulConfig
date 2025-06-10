package moe.nea.shale.layout

import moe.nea.shale.render.BackgroundPainter
import moe.nea.shale.util.AllOpen

@AllOpen
class Element {
    val children: MutableList<Element> = mutableListOf()
    var relativePosition = Position.ZERO
    var absolutePosition: Position = Position.ZERO
    var preferredSize = Size.ZERO
    var minimumSize = Size.ZERO
    var parent: Element? = null
    var padding: Padding = Padding.NONE
    var direction: LayoutDirection = LayoutDirection.LEFT_TO_RIGHT
    var backgroundPainter: BackgroundPainter? = null

    // TODO: split up to along axis / cross axis
    var sizing: Sizing = Sizing.Fit

    var childGap: Int = 0

    val area get() = Area(absolutePosition, preferredSize)

    fun beforePass(layoutPass: LayoutPass) {
        when (layoutPass) {
            is LayoutPass.Adopt -> {
                children.forEach { it.parent = this }
            }

            is LayoutPass.Render -> {
                backgroundPainter?.paint(layoutPass.graphicsContext, area)
            }

            is LayoutPass.Reset -> {
                relativePosition = Position.ZERO
                absolutePosition = Position.ZERO
                preferredSize = Size.ZERO
                minimumSize = Size.ZERO
            }

            is LayoutPass.AbsolutePosition -> {
                parent?.let { parent ->
                    absolutePosition = parent.absolutePosition + relativePosition
                }
            }

            else -> {}
        }
    }

    fun afterPass(layoutPass: LayoutPass) {}

    fun visit(layoutPass: LayoutPass) {
        beforePass(layoutPass)
        children.forEach { layoutPass.visitChild(it) }
        afterPass(layoutPass)
    }

    fun constrainPreferredSize() {
        // TODO: also apply maximumSize here
        preferredSize = Size(
            maxOf(minimumSize.width, preferredSize.width),
            maxOf(minimumSize.height, preferredSize.height),
        )
    }
}
