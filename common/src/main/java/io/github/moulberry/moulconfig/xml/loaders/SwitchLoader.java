package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.SwitchComponent;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class SwitchLoader implements XMLGuiLoader<SwitchComponent> {
    @Override
    public @NotNull SwitchComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        var value = context.getPropertyFromAttribute(element, new QName("value"), Boolean.class);
        var time = context.getPropertyFromAttribute(element, new QName("animationSpeed"), Integer.class);
        return new SwitchComponent(value, time == null ? 100 : time.get());
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Switch");
    }

}
