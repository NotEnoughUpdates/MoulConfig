package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.component.PanelComponent;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class PanelLoader implements XMLGuiLoader.Basic<PanelComponent> {
    @Override public PanelComponent createInstance(XMLContext<?> context, Element element) {
        return new PanelComponent(
            context.getChildFragment(element),
            context.getPropertyFromAttribute(element, new QName("insets"), Integer.class, 2),
            context.getPropertyFromAttribute(element, new QName("background"), PanelComponent.BackgroundRenderer.class, PanelComponent.DefaultBackgroundRenderer.DARK_RECT)
        );
    }
    @Override public QName getName() { return XMLUniverse.qName("Panel"); }
    @Override public ChildCount getChildCount() { return ChildCount.ONE; }
    @Override public Map<String, Boolean> getAttributeNames() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("insets", false);
        map.put("background", false);
        return map;
    }
}
