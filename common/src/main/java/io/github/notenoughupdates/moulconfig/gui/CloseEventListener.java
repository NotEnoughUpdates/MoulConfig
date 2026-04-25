package io.github.notenoughupdates.moulconfig.gui;

/**
 * Implement this interface alongside {@link GuiComponent} to receive an event when the gui closes.
 */
public interface CloseEventListener {
    enum CloseAction {
        NO_OBJECTIONS_TO_CLOSE,
        DENY_CLOSE;

        public CloseAction or(CloseAction other) {
            if (this == DENY_CLOSE) {
                return this;
            }
            return other;
        }
    }

    /**
     * Called just before a voluntary close. Return {@link CloseAction#DENY_CLOSE} to override the close. Make sure to update
     * your state such that the user can close the gui afterward.
     */
    default CloseAction onBeforeClose() {
        return CloseAction.NO_OBJECTIONS_TO_CLOSE;
    }

    /**
     * Called after the gui has been closed, both by the component gui, or by other actors, such as a teleport packet.
     */
    default void onAfterClose() {
    }
}
