package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.gui.component.CenterComponent
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class CenterLoader : XMLGuiLoader.Basic<CenterComponent> {
    override fun createInstance(
        context: XMLContext<*>,
        element: Element
    ): CenterComponent {
        return CenterComponent(
            context.getChildFragment(
                element
            )
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Center")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.ONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf()
    }
}
