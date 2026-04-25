package io.github.notenoughupdates.moulconfig.common;

/**
 * Not for manual implementation. This should be implemented by the corresponding platform.
 *
 * @see IMinecraft#getKeyboardConstants()
 * @see KeyboardConstants
 */
public interface IKeyboardConstants {
    int getBackSpace();
    int getCtrlLeft();
    int getCtrlRight();
    int getCmdLeft();
    int getCmdRight();
    int getShiftLeft();
    int getShiftRight();
    int getEscape();
    int getNone();
    int getEnter();
    int getDelete();
    int getUp();
    int getDown();
    int getRight();
    int getLeft();
    int getHome();
    int getEnd();
    int getKeyA();
    int getKeyC();
    int getKeyX();
    int getKeyV();
    int getKeyN();
    int getKeyF();
}
