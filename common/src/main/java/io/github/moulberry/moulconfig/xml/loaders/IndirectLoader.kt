package io.github.moulberry.moulconfig.xml.loaders

import io.github.moulberry.moulconfig.gui.GuiComponent
import io.github.moulberry.moulconfig.gui.component.IndirectComponent
import io.github.moulberry.moulconfig.xml.ChildCount
import io.github.moulberry.moulconfig.xml.XMLContext
import io.github.moulberry.moulconfig.xml.XMLGuiLoader
import io.github.moulberry.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class IndirectLoader : XMLGuiLoader<IndirectComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): IndirectComponent {
        return IndirectComponent(context.getPropertyFromAttribute(element, QName("value"), GuiComponent::class.java)!!)
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