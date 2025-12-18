package io.github.notenoughupdates.moulconfig.platform;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.notenoughupdates.moulconfig.common.IKeyboardConstants;

public class ModernKeyboardConstants implements IKeyboardConstants {
    public static final ModernKeyboardConstants INSTANCE = new ModernKeyboardConstants();

    private ModernKeyboardConstants() {}

    @Override
    public int getBackSpace() {
        return InputConstants.KEY_BACKSPACE;
    }

    @Override
    public int getCtrlLeft() {
        return InputConstants.KEY_LCONTROL;
    }

    @Override
    public int getCtrlRight() {
        return InputConstants.KEY_RCONTROL;
    }

    @Override
    public int getCmdLeft() {
        return InputConstants.#if MC > 12107 KEY_LSUPER #else KEY_LWIN #endif;
    }

    @Override
    public int getCmdRight() {
        return InputConstants.#if MC > 12107 KEY_RSUPER #else KEY_RWIN #endif;
    }

    @Override
    public int getShiftLeft() {
        return InputConstants.KEY_LSHIFT;
    }

    @Override
    public int getShiftRight() {
        return InputConstants.KEY_RSHIFT;
    }

    @Override
    public int getEscape() {
        return InputConstants.KEY_ESCAPE;
    }

    @Override
    public int getNone() {
        return -1;
    }

    @Override
    public int getEnter() {
        return InputConstants.KEY_RETURN;
    }

    @Override
    public int getDelete() {
        return InputConstants.KEY_DELETE;
    }

    @Override
    public int getUp() {
        return InputConstants.KEY_UP;
    }

    @Override
    public int getDown() {
        return InputConstants.KEY_DOWN;
    }

    @Override
    public int getRight() {
        return InputConstants.KEY_RIGHT;
    }

    @Override
    public int getLeft() {
        return InputConstants.KEY_LEFT;
    }

    @Override
    public int getHome() {
        return InputConstants.KEY_HOME;
    }

    @Override
    public int getEnd() {
        return InputConstants.KEY_END;
    }

    @Override
    public int getKeyA() {
        return InputConstants.KEY_A;
    }

    @Override
    public int getKeyC() {
        return InputConstants.KEY_C;
    }

    @Override
    public int getKeyX() {
        return InputConstants.KEY_X;
    }

    @Override
    public int getKeyV() {
        return InputConstants.KEY_V;
    }

    @Override
    public int getKeyN() {
        return InputConstants.KEY_N;
    }

    @Override
    public int getKeyF() {
        return InputConstants.KEY_F;
    }
}
