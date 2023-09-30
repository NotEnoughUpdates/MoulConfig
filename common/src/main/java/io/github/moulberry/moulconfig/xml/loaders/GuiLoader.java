package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.component.CenterComponent;
import io.github.moulberry.moulconfig.gui.component.PanelComponent;
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

public class GuiLoader implements XMLGuiLoader<CenterComponent> {
    @Override
    public @NotNull CenterComponent createInstance(@NotNull XMLContext<?> context, @NotNull Element element) {
        return new CenterComponent(new PanelComponent(context.getChildFragment(element)));
    }

    @Override
    public @NotNull QName getName() {
        return XMLUniverse.qName("Gui");
    }

    @Override
    public @NotNull ChildCount getChildCount() {
        return ChildCount.ONE;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, Boolean> getAttributeNames() {
        return MapOfs.of();
    }
}
