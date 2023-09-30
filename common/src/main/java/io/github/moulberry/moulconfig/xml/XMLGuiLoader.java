package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.gui.GuiComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Map;

public interface XMLGuiLoader<T extends GuiComponent> {
    @NotNull T createInstance(@NotNull XMLContext<?> context, @NotNull Element element);

    @NotNull QName getName();

    @NotNull ChildCount getChildCount();

    @NotNull @Unmodifiable Map<String, Boolean> getAttributeNames();
}
