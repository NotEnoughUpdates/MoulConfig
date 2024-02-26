package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.component.SwitchComponent;
import io.github.notenoughupdates.moulconfig.internal.MapOfs;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Map;

public class SwitchLoader implements XMLGuiLoader<SwitchComponent> {
    @Override
    public @NotNull SwitchComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        var value = context.getPropertyFromAttribute(element, new QName("value"), Boolean.class);
        var time = context.getPropertyFromAttribute(element, new QName("animationSpeed"), Integer.class);
        return new SwitchComponent(value, time == null ? 100 : time.get());
    }

    @Override
    public @NotNull QName getName() {
        return XMLUniverse.qName("Switch");
    }

    @Override
    public @NotNull ChildCount getChildCount() {
        return ChildCount.NONE;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Boolean> getAttributeNames() {
        return MapOfs.of("value", true, "animationSpeed", false);
    }

}
