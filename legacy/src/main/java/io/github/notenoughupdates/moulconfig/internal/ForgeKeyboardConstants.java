package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.common.IKeyboardConstants;
import org.lwjgl.input.Keyboard;

public final class ForgeKeyboardConstants implements IKeyboardConstants {
    public static final ForgeKeyboardConstants INSTANCE = new ForgeKeyboardConstants();

    private ForgeKeyboardConstants() {
    }

    @Override public int getBackSpace() { return Keyboard.KEY_BACK; }
    @Override public int getCtrlLeft() { return Keyboard.KEY_LCONTROL; }
    @Override public int getCtrlRight() { return Keyboard.KEY_RCONTROL; }
    @Override public int getCmdLeft() { return Keyboard.KEY_LMETA; }
    @Override public int getCmdRight() { return Keyboard.KEY_RMETA; }
    @Override public int getShiftLeft() { return Keyboard.KEY_LSHIFT; }
    @Override public int getShiftRight() { return Keyboard.KEY_RSHIFT; }
    @Override public int getEscape() { return Keyboard.KEY_ESCAPE; }
    @Override public int getNone() { return Keyboard.KEY_NONE; }
    @Override public int getEnter() { return Keyboard.KEY_RETURN; }
    @Override public int getDelete() { return Keyboard.KEY_DELETE; }
    @Override public int getUp() { return Keyboard.KEY_UP; }
    @Override public int getDown() { return Keyboard.KEY_DOWN; }
    @Override public int getRight() { return Keyboard.KEY_RIGHT; }
    @Override public int getLeft() { return Keyboard.KEY_LEFT; }
    @Override public int getHome() { return Keyboard.KEY_HOME; }
    @Override public int getEnd() { return Keyboard.KEY_END; }
    @Override public int getKeyA() { return Keyboard.KEY_A; }
    @Override public int getKeyC() { return Keyboard.KEY_C; }
    @Override public int getKeyX() { return Keyboard.KEY_X; }
    @Override public int getKeyV() { return Keyboard.KEY_V; }
    @Override public int getKeyN() { return Keyboard.KEY_N; }
    @Override public int getKeyF() { return Keyboard.KEY_F; }
}
