package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public interface XMLGuiLoader<T extends GuiComponent> {
    @NotNull T createInstance(@NotNull XMLContext<?> context, @NotNull Element element);

    QName getName();
}
