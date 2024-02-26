package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.gui.component.SpacerComponent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class SpacerLoader : XMLGuiLoader<SpacerComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): SpacerComponent {
        return SpacerComponent(
            context.getPropertyFromAttribute(element, QName("width"), Int::class.java) ?: GetSetter.constant(0),
            context.getPropertyFromAttribute(element, QName("height"), Int::class.java) ?: GetSetter.constant(0),
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Spacer")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.NONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("width" to false, "height" to false)
    }
}