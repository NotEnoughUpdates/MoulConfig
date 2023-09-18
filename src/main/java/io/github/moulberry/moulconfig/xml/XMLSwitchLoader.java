package io.github.moulberry.moulconfig.xml;

import io.github.moulberry.moulconfig.gui.elements.GuiElementSwitch;
import lombok.var;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class XMLSwitchLoader implements XMLGuiLoader<GuiElementSwitch> {
    @Override
    public GuiElementSwitch createInstance(XMLContext<?> context, Element element) {
        var value = context.getPropertyFromAttribute(element, new QName("value"), Boolean.class);
        var time = context.getPropertyFromAttribute(element, new QName("animationSpeed"), Integer.class);
        return new GuiElementSwitch(value, time == null ? 100 : time.get());
    }

    @Override
    public QName getName() {
        return XMLUniverse.qName("Switch");
    }
}
