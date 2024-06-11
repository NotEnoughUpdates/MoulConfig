package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.component.SliderComponent;
import io.github.notenoughupdates.moulconfig.internal.MapOfs;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Map;

public class SliderLoader implements XMLGuiLoader.Basic<SliderComponent> {
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
    public @NotNull QName getName() {
        return XMLUniverse.qName("Slider");
    }

    @Override
    public @NotNull ChildCount getChildCount() {
        return ChildCount.NONE;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Boolean> getAttributeNames() {
        return MapOfs.of("value", true, "minValue", true, "maxValue", true, "minStep", false, "width", false);
    }
}
