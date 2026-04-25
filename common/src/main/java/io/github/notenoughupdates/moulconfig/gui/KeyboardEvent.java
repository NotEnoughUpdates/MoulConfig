package io.github.notenoughupdates.moulconfig.gui;

import java.util.Objects;

public interface KeyboardEvent {
    final class CharTyped implements KeyboardEvent {
        private final char character;

        public CharTyped(char character) {
            this.character = character;
        }

        public char getChar() {
            return character;
        }

        public char component1() {
            return character;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharTyped)) return false;
            CharTyped charTyped = (CharTyped) o;
            return character == charTyped.character;
        }

        @Override
        public int hashCode() {
            return Character.hashCode(character);
        }

        @Override
        public String toString() {
            return "CharTyped(char=" + character + ")";
        }
    }

    final class KeyPressed implements KeyboardEvent {
        private final int keycode;
        private final int scancode;
        private final boolean pressed;

        public KeyPressed(int keycode, int scancode, boolean pressed) {
            this.keycode = keycode;
            this.scancode = scancode;
            this.pressed = pressed;
        }

        public int getKeycode() {
            return keycode;
        }

        public int getScancode() {
            return scancode;
        }

        public boolean getPressed() {
            return pressed;
        }

        public boolean isPressed() {
            return pressed;
        }

        public int component1() {
            return keycode;
        }

        public int component2() {
            return scancode;
        }

        public boolean component3() {
            return pressed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KeyPressed)) return false;
            KeyPressed that = (KeyPressed) o;
            return keycode == that.keycode && scancode == that.scancode && pressed == that.pressed;
        }

        @Override
        public int hashCode() {
            return Objects.hash(keycode, scancode, pressed);
        }

        @Override
        public String toString() {
            return "KeyPressed(keycode=" + keycode + ", scancode=" + scancode + ", pressed=" + pressed + ")";
        }
    }
}
