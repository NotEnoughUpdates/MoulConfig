package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.SliderComponent;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class SliderLoader implements XMLGuiLoader<SliderComponent> {
    @Override
    public @NotNull SliderComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        return new SliderComponent(
                context.getPropertyFromAttribute(element, new QName("value"), Float.class),
                context.getPropertyFromAttribute(element, new QName("minValue"), Float.class).get(),
                context.getPropertyFromAttribute(element, new QName("maxValue"), Float.class).get(),
                context.getPropertyFromAttribute(element, new QName("minStep"), Float.class, 1F),
                context.getPropertyFromAttribute(element, new QName("width"), Integer.class, 80)
        );
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Slider");
    }
}
