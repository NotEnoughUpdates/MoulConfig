package io.github.notenoughupdates.moulconfig.xml;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Map;

public interface XMLGuiLoader<T extends GuiComponent> {
    @NotNull
    T createInstance(@NotNull XMLContext<?> context, @NotNull Element element);

    @NotNull
    QName getName();

    @NotNull
    Element emitXSDType(@NotNull XSDGenerator generator, @NotNull Element root);

    interface Basic<T extends GuiComponent> extends XMLGuiLoader<T> {
        @NotNull
        ChildCount getChildCount();

        @NotNull
        @Unmodifiable
        Map<String, Boolean> getAttributeNames();

        @Override
        default @NotNull Element emitXSDType(@NotNull XSDGenerator generator, @NotNull Element root) {
            return generator.emitBasicType(this);
        }
    }
}
