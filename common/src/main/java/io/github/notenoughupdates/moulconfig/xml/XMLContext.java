package io.github.notenoughupdates.moulconfig.xml;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.internal.CollectionUtils;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class XMLContext<T> {
    private final XMLUniverse universe;
    private final T boundObject;

    public XMLContext(XMLUniverse universe, T boundObject) {
        this.universe = universe;
        this.boundObject = boundObject;
    }

    public XMLUniverse getUniverse() {
        return universe;
    }

    public T getBoundObject() {
        return boundObject;
    }

    public GuiComponent getChildFragment(Element element) {
        return CollectionUtils.getSingleOrThrow(getChildFragments(element, this));
    }

    public GuiComponent getChildFragment(Element element, Object rebind) {
        return CollectionUtils.getSingleOrThrow(getChildFragments(element, new XMLContext<>(universe, rebind)));
    }

    public List<GuiComponent> getChildFragments(Element element) {
        return getChildFragments(element, this);
    }

    public List<GuiComponent> getChildFragments(Element element, Object rebind) {
        return getChildFragments(element, new XMLContext<>(universe, rebind));
    }

    public List<GuiComponent> getChildFragments(Element element, XMLContext<?> context) {
        org.w3c.dom.NodeList childNodes = element.getChildNodes();
        List<GuiComponent> list = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            org.w3c.dom.Node item = childNodes.item(i);
            if (item instanceof Element) {
                list.add(universe.load(context, (Element) item));
            }
        }
        return list;
    }

    public <E> E getPropertyFromAttribute(Element element, QName name, Class<E> type, E def) {
        GetSetter<E> prop = getPropertyFromAttribute(element, name, type);
        return prop == null ? def : prop.get();
    }

    private String getRawXMLValue(Element element, QName name) {
        if (!XMLConstants.NULL_NS_URI.equals(name.getNamespaceURI())) {
            Warnings.warn("Attributes should not have a namespace attached to them. This namespace will be ignored");
        }
        String attributeValue = element.getAttribute(name.getLocalPart());
        return attributeValue.isEmpty() ? null : attributeValue;
    }

    public <E> GetSetter<E> getPropertyFromAttribute(Element element, QName name, Class<E> type) {
        String attributeValue = getRawXMLValue(element, name);
        if (attributeValue == null) return null;
        if (attributeValue.startsWith("@")) {
            return getBoundProperty(attributeValue.substring(1), type);
        }
        E e = universe.mapXMLObject(attributeValue, type);
        return new GetSetter<E>() {
            @Override
            public E get() {
                return e;
            }

            @Override
            public void set(E newValue) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public <E> Consumer<E> getMethodFromAttribute(Element element, QName name, Class<E> type) {
        String attribute = getRawXMLValue(element, name);
        if (attribute == null) return value -> {};
        if (!attribute.startsWith("@")) throw new RuntimeException("Object bound method without @ prefix " + attribute + " at " + name);
        return getBoundMethod(attribute.substring(1), type);
    }

    public Runnable getMethodFromAttribute(Element element, QName name) {
        String attribute = getRawXMLValue(element, name);
        if (attribute == null) return () -> {};
        if (!attribute.startsWith("@")) throw new RuntimeException("Object bound method without @ prefix " + attribute + " at " + name);
        return getBoundMethod(attribute.substring(1));
    }

    public <E> Consumer<E> getBoundMethod(String name, Class<E> argument) {
        return universe.getPropertyFinder(boundObject.getClass()).getBoundFunction(name, boundObject, argument);
    }

    public Runnable getBoundMethod(String name) {
        return universe.getPropertyFinder(boundObject.getClass()).getBoundFunction(name, boundObject);
    }

    public <E> GetSetter<E> getBoundProperty(String name, Class<E> type) {
        return universe.getPropertyFinder(boundObject.getClass()).getBoundProperty(name, type, boundObject);
    }
}
