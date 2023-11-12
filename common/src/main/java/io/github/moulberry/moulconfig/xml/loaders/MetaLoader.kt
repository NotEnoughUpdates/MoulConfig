package io.github.moulberry.moulconfig.xml.loaders

import io.github.moulberry.moulconfig.gui.CloseEventListener
import io.github.moulberry.moulconfig.gui.component.MetaComponent
import io.github.moulberry.moulconfig.xml.ChildCount
import io.github.moulberry.moulconfig.xml.XMLContext
import io.github.moulberry.moulconfig.xml.XMLGuiLoader
import io.github.moulberry.moulconfig.xml.XMLUniverse
import org.w3c.dom.Element
import javax.xml.namespace.QName

class MetaLoader : XMLGuiLoader<MetaComponent> {
    override fun createInstance(context: XMLContext<*>, element: Element): MetaComponent {
        val beforeClose =
            context.getPropertyFromAttribute(element, QName("beforeClose"), CloseEventListener.CloseAction::class.java)
        val afterClose = context.getMethodFromAttribute(element, QName("afterClose"))
        val requestClose = context.getPropertyFromAttribute(element, QName("requestClose"), Runnable::class.java)
        return MetaComponent(beforeClose, afterClose, requestClose)
    }

    override fun getName(): QName {
        return XMLUniverse.qName("Meta")
    }

    override fun getChildCount(): ChildCount {
        return ChildCount.NONE
    }

    override fun getAttributeNames(): Map<String, Boolean> {
        return mapOf("beforeClose" to false, "afterClose" to false, "requestClose" to false)
    }
}