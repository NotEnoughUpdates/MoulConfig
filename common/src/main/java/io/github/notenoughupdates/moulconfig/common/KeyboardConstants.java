package io.github.notenoughupdates.moulconfig.common;

public final class KeyboardConstants {
    public static final KeyboardConstants INSTANCE = new KeyboardConstants();

    private KeyboardConstants() {
    }

    private static IKeyboardConstants constants() {
        return IMinecraft.getInstance().getKeyboardConstants();
    }

    public static int getBackSpace() { return constants().getBackSpace(); }
    public static int getCtrlLeft() { return constants().getCtrlLeft(); }
    public static int getCtrlRight() { return constants().getCtrlRight(); }
    public static int getCmdLeft() { return constants().getCmdLeft(); }
    public static int getCmdRight() { return constants().getCmdRight(); }
    public static int getShiftLeft() { return constants().getShiftLeft(); }
    public static int getShiftRight() { return constants().getShiftRight(); }
    public static int getEscape() { return constants().getEscape(); }
    public static int getNone() { return constants().getNone(); }
    public static int getEnter() { return constants().getEnter(); }
    public static int getDelete() { return constants().getDelete(); }
    public static int getUp() { return constants().getUp(); }
    public static int getDown() { return constants().getDown(); }
    public static int getRight() { return constants().getRight(); }
    public static int getLeft() { return constants().getLeft(); }
    public static int getHome() { return constants().getHome(); }
    public static int getEnd() { return constants().getEnd(); }
    public static int getKeyA() { return constants().getKeyA(); }
    public static int getKeyC() { return constants().getKeyC(); }
    public static int getKeyX() { return constants().getKeyX(); }
    public static int getKeyV() { return constants().getKeyV(); }
    public static int getKeyN() { return constants().getKeyN(); }
    public static int getKeyF() { return constants().getKeyF(); }

    public int getBackSpaceValue() { return getBackSpace(); }
    public int getCtrlLeftValue() { return getCtrlLeft(); }
    public int getCtrlRightValue() { return getCtrlRight(); }
    public int getCmdLeftValue() { return getCmdLeft(); }
    public int getCmdRightValue() { return getCmdRight(); }
    public int getShiftLeftValue() { return getShiftLeft(); }
    public int getShiftRightValue() { return getShiftRight(); }
    public int getEscapeValue() { return getEscape(); }
    public int getNoneValue() { return getNone(); }
    public int getEnterValue() { return getEnter(); }
    public int getDeleteValue() { return getDelete(); }
    public int getUpValue() { return getUp(); }
    public int getDownValue() { return getDown(); }
    public int getRightValue() { return getRight(); }
    public int getLeftValue() { return getLeft(); }
    public int getHomeValue() { return getHome(); }
    public int getEndValue() { return getEnd(); }
    public int getKeyAValue() { return getKeyA(); }
    public int getKeyCValue() { return getKeyC(); }
    public int getKeyXValue() { return getKeyX(); }
    public int getKeyVValue() { return getKeyV(); }
    public int getKeyNValue() { return getKeyN(); }
    public int getKeyFValue() { return getKeyF(); }
}
