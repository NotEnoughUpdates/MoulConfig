package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.notenoughupdates.moulconfig.gui.component.CollapsibleComponent
import io.github.notenoughupdates.moulconfig.gui.component.TextComponent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class BasicCollapsibleLoader : XMLGuiLoader<CollapsibleComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): CollapsibleComponent {
        val state = context.getPropertyFromAttribute(element, QName("value"), Boolean::class.java)
            ?: GetSetter.floating(true)
        val body = context.getChildFragment(element)
        val title = context.getPropertyFromAttribute(element, QName("title"), String::class.java)!!
        val textComponent = TextComponent(
            IMinecraft.instance.defaultFontRenderer,
            title,
            IMinecraft.instance.defaultFontRenderer.getStringWidth(title.get()),
            TextComponent.TextAlignment.LEFT,
            false,
            false
        )
        return CollapsibleComponent(
            { textComponent },
            { body },
            state
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Collapsible")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.ONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("title" to true, "value" to false)
    }
}