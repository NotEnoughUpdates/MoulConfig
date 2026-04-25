package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.GuiComponent;
import io.github.notenoughupdates.moulconfig.gui.component.IndirectComponent;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Map;

public class IndirectLoader implements XMLGuiLoader.Basic<IndirectComponent> {
    @Override public IndirectComponent createInstance(XMLContext<?> context, Element element) {
        return new IndirectComponent(context.getPropertyFromAttribute(element, new QName("value"), GuiComponent.class));
    }
    @Override public QName getName() { return XMLUniverse.qName("Indirect"); }
    @Override public ChildCount getChildCount() { return ChildCount.NONE; }
    @Override public Map<String, Boolean> getAttributeNames() { return Collections.singletonMap("value", true); }
}
