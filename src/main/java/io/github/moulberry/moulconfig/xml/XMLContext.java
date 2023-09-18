package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import io.github.moulberry.moulconfig.internal.CollectionUtils;
import io.github.moulberry.moulconfig.internal.Warnings;
import io.github.moulberry.moulconfig.observer.GetSetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class XMLContext<T> {
    final XMLUniverse universe;
    final T object;

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
        NodeList childNodes = element.getChildNodes();
        List<GuiComponent> list = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element) {
                GuiComponent loadedFragment = universe.load(context, (Element) item);
                list.add(loadedFragment);
            }
        }
        return list;
    }

    public <E> E getPropertyFromAttribute(Element element, QName name, Class<E> type, E def) {
        GetSetter<E> prop = getPropertyFromAttribute(element, name, type);
        if (prop == null) {
            return def;
        }
        return prop.get();
    }

    private String getRawXMLValue(Element element, QName name) {
        if (!name.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
            Warnings.warn("Attributes should not have a namespace attached to them. This namespace will be ignored");
        }
        var attributeValue = element.getAttribute(name.getLocalPart());
        if (attributeValue.isEmpty()) return null;
        return attributeValue;
    }

    public <E> @Nullable GetSetter<E> getPropertyFromAttribute(@NotNull Element element, @NotNull QName name, @NotNull Class<E> type) {
        var attributeValue = getRawXMLValue(element, name);
        if (attributeValue == null) return null;
        if (attributeValue.startsWith("@")) {
            return getBoundProperty(attributeValue.substring(1), type);
        }
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


    public <E> Consumer<E> getMethodFromAttribute(Element element, QName name, Class<E> type) {
        String attribute = getRawXMLValue(element, name);
        if (attribute == null) return e -> {
        };
        if (!attribute.startsWith("@"))
            throw new RuntimeException("Object bound method without @ prefix " + attribute + " at " + name);
        return getBoundMethod(attribute.substring(1), type);
    }

    public Runnable getMethodFromAttribute(Element element, QName name) {
        String attribute = getRawXMLValue(element, name);
        if (attribute == null) return () -> {
        };
        if (!attribute.startsWith("@"))
            throw new RuntimeException("Object bound method without @ prefix " + attribute + " at " + name);
        return getBoundMethod(attribute.substring(1));
    }

    public <E> Consumer<E> getBoundMethod(String name, Class<E> argument) {
        return universe.getPropertyFinder(object.getClass()).getBoundFunction(name, object, argument);
    }

    public Runnable getBoundMethod(String name) {
        return universe.getPropertyFinder(object.getClass()).getBoundFunction(name, object);
    }

    public <E> GetSetter<E> getBoundProperty(String name, Class<E> type) {
        var propertyFinder = universe.getPropertyFinder(object.getClass());
        return propertyFinder.getBoundProperty(name, type, object);
    }

}
