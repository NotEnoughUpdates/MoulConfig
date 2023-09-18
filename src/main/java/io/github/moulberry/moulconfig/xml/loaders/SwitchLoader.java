package io.github.moulberry.moulconfig.xml.loaders;

import io.github.moulberry.moulconfig.gui.elements.GuiElementSwitch;
import io.github.moulberry.moulconfig.xml.XMLContext;
import io.github.moulberry.moulconfig.xml.XMLGuiLoader;
import io.github.moulberry.moulconfig.xml.XMLUniverse;
import lombok.var;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;

public class SwitchLoader implements XMLGuiLoader<GuiElementSwitch> {
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
