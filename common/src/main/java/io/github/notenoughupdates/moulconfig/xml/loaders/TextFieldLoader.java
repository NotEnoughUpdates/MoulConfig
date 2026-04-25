package io.github.notenoughupdates.moulconfig.xml.loaders;

import io.github.notenoughupdates.moulconfig.gui.component.TextFieldComponent;
import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import io.github.notenoughupdates.moulconfig.xml.ChildCount;
import io.github.notenoughupdates.moulconfig.xml.XMLContext;
import io.github.notenoughupdates.moulconfig.xml.XMLGuiLoader;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class TextFieldLoader implements XMLGuiLoader.Basic<TextFieldComponent> {
    @Override public TextFieldComponent createInstance(XMLContext<?> context, Element element) {
        GetSetter<Boolean> editable = context.getPropertyFromAttribute(element, new QName("editable"), Boolean.class);
        if (editable == null) editable = GetSetter.constant(true);
        return new TextFieldComponent(
            context.getPropertyFromAttribute(element, new QName("value"), String.class),
            context.getPropertyFromAttribute(element, new QName("width"), Integer.class, 80),
            editable,
            context.getPropertyFromAttribute(element, new QName("suggestion"), String.class, "")
        );
    }
    @Override public QName getName() { return XMLUniverse.qName("TextField"); }
    @Override public ChildCount getChildCount() { return ChildCount.NONE; }
    @Override public Map<String, Boolean> getAttributeNames() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("value", true);
        map.put("width", false);
        map.put("editable", false);
        map.put("suggestion", false);
        return map;
    }
}
