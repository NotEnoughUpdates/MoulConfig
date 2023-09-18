package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.internal.CollectionUtils;
import io.github.moulberry.moulconfig.internal.Warnings;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class XMLContext<T> {
    final XMLUniverse universe;
    final T object;

    public GuiElementNew getChildFragment(Element element) {
        return CollectionUtils.getSingleOrThrow(getChildFragments(element, this));
    }

    public GuiElementNew getChildFragment(Element element, Object rebind) {
        return CollectionUtils.getSingleOrThrow(getChildFragments(element, new XMLContext<>(universe, rebind)));
    }

    public List<GuiElementNew> getChildFragments(Element element) {
        return getChildFragments(element, this);
    }

    public List<GuiElementNew> getChildFragments(Element element, Object rebind) {
        return getChildFragments(element, new XMLContext<>(universe, rebind));
    }

    public List<GuiElementNew> getChildFragments(Element element, XMLContext<?> context) {
        NodeList childNodes = element.getChildNodes();
        List<GuiElementNew> list = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element) {
                GuiElementNew loadedFragment = universe.load(context, (Element) item);
                list.add(loadedFragment);
            }
        }
        return list;
    }

    public <E> GetSetter<E> getPropertyFromAttribute(Element element, QName name, Class<E> type) {
        if (!name.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
            Warnings.warn("Attributes should not have a namespace attached to them. This namespace will be ignored");
        }
        var attributeValue = element.getAttribute(name.getLocalPart());
        if (attributeValue.startsWith("@")) {
            return getBoundProperty(attributeValue.substring(1), type);
        }
        if (attributeValue.isEmpty()) return null;
        var e = universe.mapXMLObject(attributeValue, type);
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

    public <E> GetSetter<E> getBoundProperty(String name, Class<E> type) {
        var propertyFinder = universe.getPropertyFinder(object.getClass());
        return propertyFinder.getBoundProperty(name, type, object);
    }

}
