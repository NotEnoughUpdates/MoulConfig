package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.common.MyResourceLocation
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class FragmentLoader : XMLGuiLoader.Basic<GuiComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): GuiComponent {
        val location = context.getPropertyFromAttribute(element, QName("value"), MyResourceLocation::class.java)!!.get()
        val bind = context.getPropertyFromAttribute(element, QName("bind"), Any::class.java)?.get() ?: element
        return context.universe.load(bind, location)
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Fragment")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.NONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("value" to true, "bind" to false)
    }
}
