package io.github.notenoughupdates.moulconfig.xml.loaders

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.component.ArrayComponent
import io.github.notenoughupdates.moulconfig.internal.MapOfs
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import io.github.notenoughupdates.moulconfig.observer.ObservableList
import io.github.notenoughupdates.moulconfig.xml.ChildCount
import io.github.notenoughupdates.moulconfig.xml.XMLContext
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse
import org.jetbrains.annotations.Unmodifiable
import org.w3c.dom.Element
import java.awt.Color
import javax.xml.namespace.QName

class ArrayLoader : XMLGuiLoader.Basic<GuiComponent?> {
    override fun createInstance(context: XMLContext<*>, element: Element): GuiComponent {
        val list = context.getPropertyFromAttribute(element, QName("data"), ObservableList::class.java)
        return ArrayComponent(
            list!!.get(),
            { context.getChildFragment(element, it) },
            context.getPropertyFromAttribute(element, QName("oddBackground"), Color::class.java)
                ?: GetSetter.constant(Color(0, true)),
            context.getPropertyFromAttribute(element, QName("evenBackground"), Color::class.java)
                ?: GetSetter.constant(Color(0, true)),
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Array")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.ONE
    }

    override fun getAttributeNames(): @Unmodifiable MutableMap<String, Boolean> {
        return MapOfs.of("data", true, "oddBackground", false, "evenBackground", false)
    }
}
