package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.ButtonComponent;
import io.github.moulberry.moulconfig.internal.MapOfs;
import io.github.moulberry.moulconfig.xml.ChildCount;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Map;

public class ButtonLoader implements XMLGuiLoader<ButtonComponent> {
    @Override
    public @NotNull ButtonComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        return new ButtonComponent(
            context.getChildFragment(element),
            context.getPropertyFromAttribute(element, new QName("margin"), int.class, 2),
            context.getMethodFromAttribute(element, new QName("onClick"))
        );
    }

    @Override
    public @NotNull QName getName() {
        return XMLUniverse.qName("Button");
    }

    @Override
    public @NotNull ChildCount getChildCount() {
        return ChildCount.ONE;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Boolean> getAttributeNames() {
        return MapOfs.of(
            "margin", false,
            "onClick", true
        );
    }
}
