package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.component.WhenComponent;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WhenLoader implements XMLGuiLoader.Basic<WhenComponent> {
    @Override public WhenComponent createInstance(XMLContext<?> context, Element element) {
        List<GuiComponent> fragments = context.getChildFragments(element);
        if (fragments.size() != 2) throw new IllegalArgumentException("When requires exactly two child fragments");
        return new WhenComponent(
            context.getPropertyFromAttribute(element, new QName("condition"), Boolean.class),
            () -> fragments.get(0),
            () -> fragments.get(1)
        );
    }
    @Override public QName getName() { return XMLUniverse.qName("When"); }
    @Override public ChildCount getChildCount() { return ChildCount.TWO; }
    @Override public Map<String, Boolean> getAttributeNames() { return Collections.singletonMap("condition", true); }
}
