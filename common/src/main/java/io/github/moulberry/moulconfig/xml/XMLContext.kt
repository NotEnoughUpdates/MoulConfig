package io.github.moulberry.moulconfig.xml

import io.github.moulberry.moulconfig.gui.GuiComponent
import io.github.moulberry.moulconfig.internal.CollectionUtils
import io.github.moulberry.moulconfig.internal.Warnings
import io.github.moulberry.moulconfig.observer.GetSetter
import lombok.Getter
import lombok.RequiredArgsConstructor
import org.w3c.dom.Element
import java.util.function.Consumer
import javax.xml.XMLConstants
import javax.xml.namespace.QName

@RequiredArgsConstructor
@Getter
class XMLContext<T : Any>(
    val universe: XMLUniverse,
    val boundObject: T,
) {
    fun getChildFragment(element: Element): GuiComponent {
        return CollectionUtils.getSingleOrThrow(getChildFragments(element, this))
    }

    fun getChildFragment(element: Element, rebind: Any): GuiComponent {
        return CollectionUtils.getSingleOrThrow(getChildFragments(element, XMLContext(universe, rebind)))
    }

    fun getChildFragments(element: Element): List<GuiComponent> {
        return getChildFragments(element, this)
    }

    fun getChildFragments(element: Element, rebind: Any): List<GuiComponent> {
        return getChildFragments(element, XMLContext(universe, rebind))
    }

    fun getChildFragments(element: Element, context: XMLContext<*>): List<GuiComponent> {
        val childNodes = element.childNodes
        val list: MutableList<GuiComponent> = ArrayList()
        for (i in 0 until childNodes.length) {
            val item = childNodes.item(i)
            if (item is Element) {
                val loadedFragment = universe.load(context, item)
                list.add(loadedFragment)
            }
        }
        return list
    }

    fun <E> getPropertyFromAttribute(element: Element, name: QName, type: Class<E>, def: E): E {
        val prop = getPropertyFromAttribute(element, name, type) ?: return def
        return prop.get()
    }

    private fun getRawXMLValue(element: Element, name: QName): String? {
        if (name.namespaceURI != XMLConstants.NULL_NS_URI) {
            Warnings.warn("Attributes should not have a namespace attached to them. This namespace will be ignored")
        }
        val attributeValue = element.getAttribute(name.localPart)
        return if (attributeValue.isEmpty()) null else attributeValue
    }

    fun <E> getPropertyFromAttribute(element: Element, name: QName, type: Class<E>): GetSetter<E>? {
        val attributeValue = getRawXMLValue(element, name) ?: return null
        if (attributeValue.startsWith("@")) {
            return getBoundProperty(attributeValue.substring(1), type)
        }
        val e = universe.mapXMLObject(attributeValue, type)
        return object : GetSetter<E> {
            override fun get(): E {
                return e
            }

            override fun set(newValue: E) {
                throw UnsupportedOperationException()
            }
        }
    }

    fun <E> getMethodFromAttribute(element: Element, name: QName, type: Class<E>): Consumer<E> {
        val attribute = getRawXMLValue(element, name) ?: return Consumer { }
        if (!attribute.startsWith("@")) throw RuntimeException("Object bound method without @ prefix $attribute at $name")
        return getBoundMethod(attribute.substring(1), type)
    }

    fun getMethodFromAttribute(element: Element, name: QName): Runnable {
        val attribute = getRawXMLValue(element, name) ?: return Runnable {}
        if (!attribute.startsWith("@")) throw RuntimeException("Object bound method without @ prefix $attribute at $name")
        return getBoundMethod(attribute.substring(1))
    }

    fun <E> getBoundMethod(name: String, argument: Class<E>): Consumer<E> {
        return universe.getPropertyFinder(boundObject.javaClass).getBoundFunction(name, boundObject, argument)
    }

    fun getBoundMethod(name: String): Runnable {
        return universe.getPropertyFinder(boundObject.javaClass).getBoundFunction(name, boundObject)
    }

    fun <E> getBoundProperty(name: String, type: Class<E>): GetSetter<E> {
        val propertyFinder = universe.getPropertyFinder(boundObject.javaClass)
        return propertyFinder.getBoundProperty(name, type, boundObject)
    }
}
