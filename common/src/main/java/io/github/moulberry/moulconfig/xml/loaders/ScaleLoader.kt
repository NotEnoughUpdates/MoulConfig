package io.github.moulberry.moulconfig.xml.loaders

import io.github.moulberry.moulconfig.gui.component.ScaleComponent
import io.github.moulberry.moulconfig.xml.ChildCount
import io.github.moulberry.moulconfig.xml.XMLContext
import io.github.moulberry.moulconfig.xml.XMLGuiLoader
import io.github.moulberry.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class ScaleLoader : XMLGuiLoader<ScaleComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): ScaleComponent {
        return ScaleComponent(
            context.getChildFragment(element),
            context.getPropertyFromAttribute(element, QName("scale"), Float::class.java)!!
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Scale")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.ONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("scale" to true)
    }
}