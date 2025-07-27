package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import lombok.var;

public class StructuredTextHelper {
    public static StructuredText mapStringOrStructuredText(Object object) {
        if (object instanceof String) {
            return StructuredText.of((String) object);
        }
        if (object instanceof StructuredText) {
            return (StructuredText) object;
        }
        var structured = IMinecraft.instance.createStructuredTextInternal(object);
        if (structured != null) {
            return structured;
        }
        throw new IllegalArgumentException("Expected string or structured text, found " + object);
    }
}
