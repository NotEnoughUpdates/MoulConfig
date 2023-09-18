package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.internal.Warnings;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

@RequiredArgsConstructor
@Getter
public class XMLContext<T> {
    final XMLUniverse universe;
    final T object;

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
