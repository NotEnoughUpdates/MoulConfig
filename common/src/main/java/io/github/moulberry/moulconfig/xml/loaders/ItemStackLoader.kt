package io.github.moulberry.moulconfig.xml.loaders

import io.github.moulberry.moulconfig.common.IItemStack
import io.github.moulberry.moulconfig.gui.component.ItemStackComponent
import io.github.moulberry.moulconfig.xml.ChildCount
import io.github.moulberry.moulconfig.xml.XMLContext
import io.github.moulberry.moulconfig.xml.XMLGuiLoader
import io.github.moulberry.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class ItemStackLoader : XMLGuiLoader<ItemStackComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): ItemStackComponent {
        return ItemStackComponent(
            context.getPropertyFromAttribute(element, QName("value"), IItemStack::class.java)!!
        )
    }

    override fun getName(): QName {
        return XMLUniverse.qName("ItemStack")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.NONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("value" to true)
    }
}