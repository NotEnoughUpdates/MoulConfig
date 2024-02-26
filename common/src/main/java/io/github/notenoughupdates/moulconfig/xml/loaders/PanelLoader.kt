package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.gui.component.PanelComponent
import io.github.notenoughupdates.moulconfig.gui.component.PanelComponent.DefaultBackgroundRenderer
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class PanelLoader : XMLGuiLoader<PanelComponent> {
    override fun createInstance(
        context: XMLContext<*>,
        element: Element
    ): PanelComponent {
        return PanelComponent(
            context.getChildFragment(
                element
            ),
            context.getPropertyFromAttribute(element, QName("insets"), Int::class.java, 2),
            context.getPropertyFromAttribute(
                element,
                QName("background"),
                PanelComponent.BackgroundRenderer::class.java,
                DefaultBackgroundRenderer.DARK_RECT
            )
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Panel")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.ONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("insets" to false, "background" to false)
    }
}