package io.github.notenoughupdates.moulconfig.xml;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.HorizontalAlign;
import io.github.notenoughupdates.moulconfig.gui.VerticalAlign;
import io.github.notenoughupdates.moulconfig.gui.component.PanelComponent;
import io.github.notenoughupdates.moulconfig.xml.loaders.*;
import lombok.SneakyThrows;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Color;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class XMLUniverse {
    public static String MOULCONFIG_XML_NS = "http://notenoughupdates.org/moulconfig";
    Map<QName, XMLGuiLoader<?>> guiElements = new HashMap<>();
    Map<Class<?>, Function<String, ?>> objectMappers = new HashMap<>();
    Map<Class<?>, XMLBoundProperties> propertiesMap = new HashMap<>();

    public static QName qName(String localPart) {
        return new QName(MOULCONFIG_XML_NS, localPart);
    }

    public static XMLUniverse getDefaultUniverse() {
        var xmlUniverse = new XMLUniverse();
        xmlUniverse.registerLoader(new SwitchLoader());
        xmlUniverse.registerLoader(new GuiLoader());
        xmlUniverse.registerLoader(new ArrayLoader());
        xmlUniverse.registerLoader(new ColumnLoader());
        xmlUniverse.registerLoader(new RowLoader());
        xmlUniverse.registerLoader(new RootLoader());
        xmlUniverse.registerLoader(new TextLoader());
        xmlUniverse.registerLoader(new ScrollPanelLoader());
        xmlUniverse.registerLoader(new TextFieldLoader());
        xmlUniverse.registerLoader(new BasicCollapsibleLoader());
        xmlUniverse.registerLoader(new ButtonLoader());
        xmlUniverse.registerLoader(new SliderLoader());
        xmlUniverse.registerLoader(new HoverLoader());
        xmlUniverse.registerLoader(new CenterLoader());
        xmlUniverse.registerLoader(new ScaleLoader());
        xmlUniverse.registerLoader(new SpacerLoader());
        xmlUniverse.registerLoader(new ItemStackLoader());
        xmlUniverse.registerLoader(new FragmentLoader());
        xmlUniverse.registerLoader(new IndirectLoader());
        xmlUniverse.registerLoader(new WhenLoader());
        xmlUniverse.registerLoader(new PanelLoader());
        xmlUniverse.registerLoader(new MetaLoader());
        xmlUniverse.registerLoader(new AlignLoader());
        xmlUniverse.registerLoader(new TabsLoader());
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
        xmlUniverse.registerMapper(List.class, str -> Arrays.asList(str.split(";")));
        xmlUniverse.registerMapper(MyResourceLocation.class, MyResourceLocation.Companion::parse);
        xmlUniverse.registerMapper(PanelComponent.BackgroundRenderer.class, PanelComponent.DefaultBackgroundRenderer::valueOf);
        xmlUniverse.registerMapper(HorizontalAlign.class, HorizontalAlign::valueOf);
        xmlUniverse.registerMapper(VerticalAlign.class, VerticalAlign::valueOf);
        xmlUniverse.registerMapper(Color.class, str -> str.startsWith("#") ? new Color((int) Long.parseLong(str.substring(1), 16), str.length() == 9) : new Color(Integer.parseInt(str), true));
        return xmlUniverse;
    }


    private XMLBoundProperties createPropertyFinder(Class<?> clazz) {
        var properties = new XMLBoundProperties();
        for (Field field : clazz.getDeclaredFields()) {
            var annotation = field.getAnnotation(Bind.class);
            if (annotation == null) continue;
            field.setAccessible(true);
            properties.getNamedProperties().put(annotation.value().isEmpty() ? field.getName() : annotation.value(), field);
        }
        for (Method method : clazz.getDeclaredMethods()) {
            var annotation = method.getAnnotation(Bind.class);
            if (annotation == null) continue;
            method.setAccessible(true);
            properties.getNamedFunctions().put(annotation.value().isEmpty() ? method.getName() : annotation.value(), method);
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
    public GuiComponent load(Object bindTo, InputStream stream) {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        var builder = factory.newDocumentBuilder();
        var document = builder.parse(stream);
        Element documentElement = document.getDocumentElement();
        XMLContext<Object> objectXMLContext = new XMLContext<>(this, bindTo);
        return load(objectXMLContext, documentElement);
    }

    public GuiComponent load(XMLContext<?> context, Element element) {
        var elementLoader = guiElements.get(new QName(element.getNamespaceURI(), element.getLocalName()));
        return elementLoader.createInstance(context, element);
    }

    @NotNull
    public GuiComponent load(@NotNull Object bind, @NotNull MyResourceLocation location) {
        return load(bind, IMinecraft.instance.loadResourceLocation(location));
    }

    public <E> E mapXMLObject(String attributeValue, Class<E> type) {
        return (E) objectMappers.get(type).apply(attributeValue);
    }
}
