package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.gui.component.ScaleComponent
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class ScaleLoader : XMLGuiLoader.Basic<ScaleComponent> {
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
