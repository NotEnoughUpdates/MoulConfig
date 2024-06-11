package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.component.IndirectComponent
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class IndirectLoader : XMLGuiLoader.Basic<IndirectComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): IndirectComponent {
        return IndirectComponent(
            context.getPropertyFromAttribute(
                element,
                QName("value"),
                GuiComponent::class.java
            )!!
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Indirect")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.NONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("value" to true)
    }
}
