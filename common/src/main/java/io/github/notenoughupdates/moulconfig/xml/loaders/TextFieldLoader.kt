package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.gui.component.TextFieldComponent
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class TextFieldLoader : XMLGuiLoader<TextFieldComponent?> {
    override fun createInstance(context: XMLContext<*>, element: Element): TextFieldComponent {
        return TextFieldComponent(
            context.getPropertyFromAttribute(element, QName("value"), String::class.java)!!,
            context.getPropertyFromAttribute(element, QName("width"), Int::class.java, 80),
            context.getPropertyFromAttribute(element, QName("editable"), Boolean::class.java)
                ?: GetSetter.constant(true),
            context.getPropertyFromAttribute(element, QName("suggestion"), String::class.java, ""),
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("TextField")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.NONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("value" to true, "width" to false, "editable" to false, "suggestion" to false)
    }
}
