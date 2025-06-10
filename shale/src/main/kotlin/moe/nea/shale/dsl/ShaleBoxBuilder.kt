package moe.nea.shale.dsl

import moe.nea.shale.layout.*
import moe.nea.shale.render.FlatShadeBackgroundPainter
import java.awt.Color

open class ShaleBoxBuilder : ShaleBuilder(), HasChildren<Element> {
    val container = BoxElement()

    fun background(color: Color) {
        background(FlatShadeBackgroundPainter(color))
    }

    fun background(color: FlatShadeBackgroundPainter) {
        container.backgroundPainter = color
    }

    @JvmOverloads
    fun grow(factor: Float = 1F) {
        container.sizing = Sizing.GrowFractional(factor)
    }

    override fun compile(): BoxElement {
        markFinalized()
        return container
    }

    override fun add(element: Element) {
        container.children.add(element)
    }

    fun direction(direction: LayoutDirection) {
        container.direction = direction
    }

    fun ttb() {
        direction(LayoutDirection.TOP_TO_BOTTOM)
    }

    fun btt() {
        direction(LayoutDirection.BOTTOM_TO_TOP)
    }

    fun rtl() {
        direction(LayoutDirection.RIGHT_TO_LEFT)
    }

    fun ltr() {
        direction(LayoutDirection.LEFT_TO_RIGHT)
    }

    fun fixed(width: Int, height: Int) {
        fixed(Size(width, height))
    }

    fun fixed(size: Size) {
        container.sizing = Sizing.Fixed(size)
    }

    fun padding(all: Int) = padding(Padding(all = all))

    fun padding(padding: Padding) {
        container.padding = padding
    }

    fun childGap(childGap: Int) {
        container.childGap = childGap
    }
}
