package moe.nea.shale.dsl

import moe.nea.shale.layout.Element
import moe.nea.shale.layout.RootElement

class ShaleRootBuilder : ShaleBuilder(), HasChildren<Element> {
    val rootElement = RootElement()
    override fun compile(): RootElement {
        markFinalized()
        return rootElement
    }

    override fun add(element: Element) {
        rootElement.children.add(element)
    }
}
