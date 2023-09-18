package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.gui.GuiElementNew;
import io.github.moulberry.moulconfig.xml.loaders.*;
import lombok.SneakyThrows;
import lombok.var;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class XMLUniverse {
    Map<QName, XMLGuiLoader<?>> guiElements = new HashMap<>();
    Map<Class<?>, Function<String, ?>> objectMappers = new HashMap<>();
    Map<Class<?>, XMLBoundProperties> propertiesMap = new HashMap<>();
    public static String MOULCONFIG_XML_NS = "http://notenoughupdates.org/moulconfig";

    public static QName qName(String localPart) {
        return new QName(MOULCONFIG_XML_NS, localPart);
    }

    public static XMLUniverse getDefaultUniverse() {
        var xmlUniverse = new XMLUniverse();
        xmlUniverse.registerLoader(new SwitchLoader());
        xmlUniverse.registerLoader(new GuiLoader());
        xmlUniverse.registerLoader(new ColumnLoader());
        xmlUniverse.registerLoader(new RowLoader());
        xmlUniverse.registerLoader(new TextLoader());
        xmlUniverse.registerMapper(String.class, Function.identity());
        xmlUniverse.registerMapper(Integer.class, Integer::valueOf);
        xmlUniverse.registerMapper(int.class, Integer::valueOf);
        xmlUniverse.registerMapper(Float.class, Float::valueOf);
        xmlUniverse.registerMapper(float.class, Float::valueOf);
        xmlUniverse.registerMapper(Double.class, Double::valueOf);
        xmlUniverse.registerMapper(double.class, Double::valueOf);
        xmlUniverse.registerMapper(Long.class, Long::valueOf);
        xmlUniverse.registerMapper(long.class, Long::valueOf);
        xmlUniverse.registerMapper(Boolean.class, Boolean::valueOf);
        xmlUniverse.registerMapper(boolean.class, Boolean::valueOf);
        return xmlUniverse;
    }


    private XMLBoundProperties createPropertyFinder(Class<?> clazz) {
        var properties = new XMLBoundProperties();
        for (Field field : clazz.getFields()) {
            var annotation = field.getAnnotation(Bind.class);
            if (annotation == null) continue;
            properties.getNamedProperties().put(annotation.value().isEmpty() ? field.getName() : annotation.value(), field);
        }
        return properties;
    }

    public <T> void registerMapper(Class<T> clazz, Function<String, T> function) {
        objectMappers.put(clazz, function);
    }

    public void registerLoader(XMLGuiLoader<?> loader) {
        guiElements.put(loader.getName(), loader);
    }

    public XMLBoundProperties getPropertyFinder(Class<?> clazz) {
        return propertiesMap.computeIfAbsent(clazz, this::createPropertyFinder);
    }

    @SneakyThrows
    public GuiElementNew load(Object bindTo, InputStream stream) {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var builder = factory.newDocumentBuilder();
        var document = builder.parse(stream);
        Element documentElement = document.getDocumentElement();
        XMLContext<Object> objectXMLContext = new XMLContext<>(this, bindTo);
        return load(objectXMLContext, documentElement);
    }

    public GuiElementNew load(XMLContext<?> context, Element element) {
        var elementLoader = guiElements.get(new QName(element.getNamespaceURI(), element.getLocalName()));
        return elementLoader.createInstance(context, element);
    }

    public <E> E mapXMLObject(String attributeValue, Class<E> type) {
        return (E) objectMappers.get(type).apply(attributeValue);
    }
}
