package io.github.notenoughupdates.moulconfig.platform;

import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import net.minecraft.client.util.InputUtil;
#if MC > 12107
import net.minecraft.client.input.KeyInput;
#endif

public class ModernKeybindHelper {
    public static StructuredText getKeyName(int keyCode) { // TODO: translations
        if (keyCode == -1) {
            return StructuredText.of("NONE");
        } else if (keyCode >= 0 && keyCode <= 9) {
            return StructuredText.of("Button " + (keyCode + 1));
        } else {
            #if MC < 12109
            StructuredText keyName = MoulConfigText.wrap(InputUtil.fromKeyCode(keyCode, 0).getLocalizedText());
            #else
            StructuredText keyName = MoulConfigText.wrap(InputUtil.fromKeyCode(new KeyInput(keyCode, 0, 0)).getLocalizedText());
            #endif
            if (keyName == null) {
                keyName = StructuredText.of("???");
            }
            return keyName;
        }
    }
}
