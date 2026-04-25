package io.github.notenoughupdates.moulconfig.gui;

import java.util.Objects;

public interface MouseEvent {
    final class Click implements MouseEvent {
        private final int mouseButton;
        private final boolean mouseState;

        public Click(int mouseButton, boolean mouseState) {
            this.mouseButton = mouseButton;
            this.mouseState = mouseState;
        }

        public int getMouseButton() {
            return mouseButton;
        }

        public boolean getMouseState() {
            return mouseState;
        }

        public int component1() {
            return mouseButton;
        }

        public boolean component2() {
            return mouseState;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Click)) return false;
            Click click = (Click) o;
            return mouseButton == click.mouseButton && mouseState == click.mouseState;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mouseButton, mouseState);
        }

        @Override
        public String toString() {
            return "Click(mouseButton=" + mouseButton + ", mouseState=" + mouseState + ")";
        }
    }

    final class Move implements MouseEvent {
        private final float dx;
        private final float dy;

        public Move(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public float getDx() {
            return dx;
        }

        public float getDy() {
            return dy;
        }

        public float component1() {
            return dx;
        }

        public float component2() {
            return dy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Move)) return false;
            Move move = (Move) o;
            return Float.compare(move.dx, dx) == 0 && Float.compare(move.dy, dy) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dx, dy);
        }

        @Override
        public String toString() {
            return "Move(dx=" + dx + ", dy=" + dy + ")";
        }
    }

    final class Scroll implements MouseEvent {
        private final float dWheel;

        public Scroll(float dWheel) {
            this.dWheel = dWheel;
        }

        public float getDWheel() {
            return dWheel;
        }

        public float component1() {
            return dWheel;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Scroll)) return false;
            Scroll scroll = (Scroll) o;
            return Float.compare(scroll.dWheel, dWheel) == 0;
        }

        @Override
        public int hashCode() {
            return Float.hashCode(dWheel);
        }

        @Override
        public String toString() {
            return "Scroll(dWheel=" + dWheel + ")";
        }
    }
}
