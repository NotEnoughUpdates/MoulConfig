package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.gui.HorizontalAlign
import io.github.notenoughupdates.moulconfig.gui.VerticalAlign
import io.github.notenoughupdates.moulconfig.gui.component.AlignComponent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class AlignLoader : XMLGuiLoader.Basic<AlignComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): AlignComponent {
        return AlignComponent(
            context.getChildFragment(element),
            context.getPropertyFromAttribute(element, QName("horizontal"), HorizontalAlign::class.java)
                ?: GetSetter.constant(HorizontalAlign.LEFT),
            context.getPropertyFromAttribute(element, QName("vertical"), VerticalAlign::class.java)
                ?: GetSetter.constant(VerticalAlign.TOP),
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Align")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.ONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("horizontal" to false, "vertical" to false)
    }
}
