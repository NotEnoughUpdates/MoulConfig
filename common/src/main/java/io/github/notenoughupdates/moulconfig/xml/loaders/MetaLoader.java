package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.CloseEventListener;
import io.github.notenoughupdates.moulconfig.gui.component.MetaComponent;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetaLoader implements XMLGuiLoader.Basic<MetaComponent> {
    @Override public MetaComponent createInstance(XMLContext<?> context, Element element) {
        return new MetaComponent(
            context.getPropertyFromAttribute(element, new QName("beforeClose"), CloseEventListener.CloseAction.class),
            context.getMethodFromAttribute(element, new QName("afterClose")),
            context.getPropertyFromAttribute(element, new QName("requestClose"), Runnable.class)
        );
    }
    @Override public QName getName() { return XMLUniverse.qName("Meta"); }
    @Override public ChildCount getChildCount() { return ChildCount.NONE; }
    @Override public Map<String, Boolean> getAttributeNames() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("beforeClose", false);
        map.put("afterClose", false);
        map.put("requestClose", false);
        return map;
    }
}
