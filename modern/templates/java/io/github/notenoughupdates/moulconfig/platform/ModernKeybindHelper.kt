package io.github.notenoughupdates.moulconfig.platform

import io.github.notenoughupdates.moulconfig.common.text.StructuredText
import net.minecraft.client.util.InputUtil
import io.github.notenoughupdates.moulconfig.platform.MoulConfigText

object ModernKeybindHelper {
    fun getKeyName(keyCode: Int): StructuredText { // TODO: translations
        if (keyCode == -1) {
            return StructuredText.of("NONE")
        } else if (keyCode in 0..9) {
            return StructuredText.of("Button ${keyCode + 1}")
        } else {
            var keyName = MoulConfigText.wrap(InputUtil.fromKeyCode(keyCode, 0).localizedText)
            if (keyName == null) {
                keyName = StructuredText.of("???")
            }
            return keyName
        }
    }
}
