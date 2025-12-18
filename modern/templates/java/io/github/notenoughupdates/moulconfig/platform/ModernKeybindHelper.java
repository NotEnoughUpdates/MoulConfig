package io.github.notenoughupdates.moulconfig.platform;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
#if MC > 12107
import net.minecraft.client.input.KeyEvent;
#endif

public class ModernKeybindHelper {
    public static StructuredText getKeyName(int keyCode) { // TODO: translations
        if (keyCode == -1) {
            return StructuredText.of("NONE");
        } else if (keyCode >= 0 && keyCode <= 9) {
            return StructuredText.of("Button " + (keyCode + 1));
        } else {
            #if MC < 12109
            StructuredText keyName = MoulConfigText.wrap(InputConstants.getKey(keyCode, 0).getDisplayName());
            #else
            StructuredText keyName = MoulConfigText.wrap(InputConstants.getKey(new KeyEvent(keyCode, 0, 0)).getDisplayName());
            #endif
            if (keyName == null) {
                keyName = StructuredText.of("???");
            }
            return keyName;
        }
    }
}
