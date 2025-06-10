package moe.nea.shale.layout

import java.awt.Color

class TextElement : Element() {
    // TODO: change hierarchy so i dont have to handle padding in each element, instead delegating to parent.
    var wrappedText: MeasuredText? = null
    var text: String = ""
        set(value) {
            field = value
            wrappedText = null
            // TODO: schedule re layout
        }
    var color: Color = Color.BLACK

    override fun beforePass(layoutPass: LayoutPass) {
        super.beforePass(layoutPass)
        when (layoutPass) {
            is LayoutPass.Fit ->  {
                preferredSize += layoutPass.axis.limit(padding.sizes)
                minimumSize += layoutPass.axis.limit(padding.sizes)
                if (layoutPass.axis == layoutPass.layoutContext.textAxis) {
                    preferredSize += layoutPass.axis.unchooseSize(layoutPass.layoutContext.measureUnwrappedText(text))
                }
            }
            is LayoutPass.Wrap -> {
                val textCharacterAxis = layoutPass.layoutContext.textAxis
                val textLineAxis = textCharacterAxis.cross
                wrappedText = layoutPass.layoutContext.measureWrappedText(text, textCharacterAxis.choose(preferredSize))
                minimumSize = textLineAxis.setUnchoosenSize(
                    minimumSize,
                    maxOf(
                        textLineAxis.choose(wrappedText!!.size) + textLineAxis.choose(padding.sizes),
                        textLineAxis.choose(minimumSize)
                    )
                )
                constrainPreferredSize()
            }

            is LayoutPass.Render -> {
                layoutPass.graphicsContext
                    .text(absolutePosition + direction.chooseBeginningPosition(padding, preferredSize), color, wrappedText ?: error("render without text wrap call passed"))
            }

            else -> {}
        }
    }

}
