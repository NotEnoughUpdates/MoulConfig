package io.github.notenoughupdates.moulconfig.platform

import net.minecraft.client.util.InputUtil

object ModernKeybindHelper {
    fun getKeyName(keyCode: Int): String {
        if (keyCode == -1) {
            return "NONE"
        } else if (keyCode in 0..9) {
            return "Button ${keyCode+1}"
        } else {
            var keyName = InputUtil.fromKeyCode(keyCode, 0).localizedText.string
            if (keyName == null) {
                keyName = "???"
            }
            return keyName
        }
    }
}
