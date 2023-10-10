package io.github.moulberry.moulconfig.xml.loaders

import io.github.moulberry.moulconfig.common.MyResourceLocation
import io.github.moulberry.moulconfig.gui.GuiComponent
import io.github.moulberry.moulconfig.xml.ChildCount
import io.github.moulberry.moulconfig.xml.XMLContext
import io.github.moulberry.moulconfig.xml.XMLGuiLoader
import io.github.moulberry.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class FragmentLoader : XMLGuiLoader<GuiComponent> {
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