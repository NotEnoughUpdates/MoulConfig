package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.TextFieldComponent;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class TextFieldLoader implements XMLGuiLoader<TextFieldComponent> {
    @Override
    public @NotNull TextFieldComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        return new TextFieldComponent(
                context.getPropertyFromAttribute(element, new QName("value"), String.class),
                context.getPropertyFromAttribute(element, new QName("editable"), boolean.class),
                context.getPropertyFromAttribute(element, new QName("suggestion"), String.class, ""),
                context.getPropertyFromAttribute(element, new QName("width"), int.class, 80)
        );
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("TextField");
    }
}
