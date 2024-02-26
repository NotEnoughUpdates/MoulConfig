/*
 * Copyright (C) 2023 NotEnoughUpdates contributors
 *
 * This file is part of MoulConfig.
 *
 * MoulConfig is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * MoulConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MoulConfig. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.github.notenoughupdates.moulconfig.internal;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeybindHelper {
    public static String getKeyName(int keyCode) {
        if (keyCode == 0) {
            return "NONE";
        } else if (keyCode < 0) {
            return "Button " + (keyCode + 101);
        } else {
            String keyName = Keyboard.getKeyName(keyCode);
            if (keyName == null) {
                keyName = "???";
            } else if (keyName.equalsIgnoreCase("LMENU")) {
                keyName = "LALT";
            } else if (keyName.equalsIgnoreCase("RMENU")) {
                keyName = "RALT";
            }
            return keyName;
        }
    }

    public static boolean isKeyValid(int keyCode) {
        return keyCode != 0;
    }

    public static boolean isKeyDown(int keyCode) {
        if (!isKeyValid(keyCode)) {
            return false;
        } else if (keyCode < 0) {
            return Mouse.isButtonDown(keyCode + 100);
        } else {
            return Keyboard.isKeyDown(keyCode);
        }
    }

    public static boolean isShiftDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }


    public static boolean isKeyPressed(int keyCode) {
        if (!isKeyValid(keyCode)) {
            return false;
        } else if (keyCode < 0) {
            return Mouse.getEventButtonState() && Mouse.getEventButton() == keyCode + 100;
        } else {
            return Keyboard.getEventKeyState() && Keyboard.getEventKey() == keyCode;
        }
    }

    public static boolean isCtrlDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }
}
