package io.github.notenoughupdates.moulconfig.xml;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.HorizontalAlign;
import io.github.notenoughupdates.moulconfig.gui.VerticalAlign;
import io.github.notenoughupdates.moulconfig.gui.component.PanelComponent;
import io.github.notenoughupdates.moulconfig.gui.component.TextComponent;
import io.github.notenoughupdates.moulconfig.internal.TypeUtils;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.loaders.*;
import io.github.notenoughupdates.moulconfig.xml.trans.UnboxGetSetter;
import io.github.notenoughupdates.moulconfig.xml.trans.UnboxPrimitives;
import lombok.SneakyThrows;
import lombok.var;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Color;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class XMLUniverse {
    public static String MOULCONFIG_XML_NS = "http://notenoughupdates.org/moulconfig";
    Map<QName, XMLGuiLoader<?>> guiElements = new HashMap<>();
    List<ParametricTypeMorphism> typeMorphisms = new ArrayList<>();
    Map<TypePath, TypeMorphismChain> composedTypeMorphisms = new HashMap<>();
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
        xmlUniverse.registerTypeMorphism(new UnboxGetSetter());
        xmlUniverse.registerTypeMorphism(new UnboxPrimitives());
        IMinecraft.getInstance().registerPlatformTypeMorphisms(xmlUniverse);
        xmlUniverse.registerMapper(List.class, str -> Arrays.asList(str.split(";")));
        xmlUniverse.registerMapper(MyResourceLocation.class, MyResourceLocation.Companion::parse);
        xmlUniverse.registerMapper(PanelComponent.BackgroundRenderer.class, PanelComponent.DefaultBackgroundRenderer::valueOf);
        xmlUniverse.registerMapper(HorizontalAlign.class, HorizontalAlign::valueOf);
        xmlUniverse.registerMapper(VerticalAlign.class, VerticalAlign::valueOf);
        xmlUniverse.registerMapper(TextComponent.TextAlignment.class, TextComponent.TextAlignment::valueOf);
        xmlUniverse.registerMapper(StructuredText.class, StructuredText::of);
        xmlUniverse.registerMapper(Color.class, str -> str.startsWith("#") ? new Color((int) Long.parseLong(str.substring(1), 16), str.length() == 9) : new Color(Integer.parseInt(str), true));
        return xmlUniverse;
    }


    private XMLBoundProperties createPropertyFinder(Class<?> clazz) {
        var properties = new XMLBoundProperties(this);
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
        registerTypeMorphism(new ParametricTypeMorphism() {
            @Override
            public String toString() {
                return "string conversion to " + clazz + " using " + function;
            }

            @Override
            public Optional<Type> codomain(Type domain) {
                if (TypeUtils.doesAExtendB(domain, String.class)) {
                    return Optional.of(clazz);
                }
                return Optional.empty();
            }

            @Override
            public GetSetter<?> apply(Type domain, GetSetter<?> value) {
                return new GetSetter<Object>() {
                    @Override
                    public Object get() {
                        return function.apply((String) value.get());
                    }

                    @Override
                    public void set(Object newValue) {
                        throw new RuntimeException("Cannot unmap string mapper");
                    }

                    @Override
                    public String toString() {
                        return "string mapped to " + clazz + " from " + value;
                    }
                };
            }
        });
    }

    @ApiStatus.Experimental
    public void registerTypeMorphism(ParametricTypeMorphism typeMorphism) {
        typeMorphisms.add(typeMorphism);
    }

    @ApiStatus.Experimental
    public TypeMorphismChain findTypeMorphismChain(Type source, Type destination) {
        return composedTypeMorphisms.computeIfAbsent(TypePath.of(source, destination),
            (path) -> findTypeMorphismChain0(path.getSource(), path.getDestination()));
    }

    private static boolean badTypeMatch(Type p, Type dest) {
        // TODO: replace this type utils check with something more solid...
        //       there are many issues:
        //          first, GetSetter is invariant wrt <T>
        //          second, this squashes all generics aside from the top level GetSetter
        //                  but who has time to reimplement the java type system at runtime?
        return TypeUtils.doesAExtendB(p, dest);
    }

    private TypeMorphismChain findTypeMorphismChain0(Type source, Type destination) {
        if (badTypeMatch(source, destination))
            return TypeMorphismChain.id(source);
        Queue<TypeMorphismChain> queue = new ArrayDeque<>();
        Set<Type> visited = new HashSet<>();
        queue.add(TypeMorphismChain.id(source));
        int stepCount = 1000;
        while (!queue.isEmpty()) {
            if (stepCount-- < 0) {
                throw new RuntimeException("MoulConfig halting problem short-circuit : failed to find a type morphism chain from " + source + " to " + destination + " after 1000 steps");
            }
            var workingChain = queue.remove();
            for (var morph : typeMorphisms) {
                var nextChain0 = workingChain.tryExtendWith(morph);
                if (nextChain0.isPresent()) {
                    var nextChain = nextChain0.get();
                    if (badTypeMatch(nextChain.getCoDomain(), destination))
                        return nextChain;
                    if (visited.add(nextChain.getCoDomain()))
                        queue.add(nextChain);
                }
            }
        }
        throw new RuntimeException("Could not find a morphism from " + source + " to " + destination + ".");
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
        return load(bind, IMinecraft.INSTANCE.loadResourceLocation(location));
    }

    @ApiStatus.Experimental
    public GetSetter<?> mapObject(GetSetter<?> getSetter, Type source, Type dest) {
        return findTypeMorphismChain(source, dest).map(getSetter);
    }

    @ApiStatus.Experimental
    public <T, R> GetSetter<R> mapObject(GetSetter<T> getSetter, Class<T> source, Class<R> dest) {
        //noinspection unchecked
        return (GetSetter<R>) mapObject(getSetter, (Type) source, (Type) dest);
    }

    public <E> E mapXMLObject(String attributeValue, Class<E> type) {
        return mapObject(GetSetter.constant(attributeValue), String.class, type)
            .get();
    }
}
