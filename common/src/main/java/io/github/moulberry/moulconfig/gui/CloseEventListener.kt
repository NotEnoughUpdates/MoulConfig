package io.github.moulberry.moulconfig.gui

/**
 * Implement this interface alongside [GuiComponent] to receive an event when the gui closes.
 */
interface CloseEventListener {
    enum class CloseAction {
        NO_OBJECTIONS_TO_CLOSE, DENY_CLOSE, ;

        fun or(other: CloseAction): CloseAction {
            if (this == DENY_CLOSE) return this
            return other
        }
    }

    /**
     * Called just before a voluntary close. Return [CloseAction.DENY_CLOSE] to override the close. Make sure to update
     * your state such that the user can close the gui afterward.
     */
    fun onBeforeClose(): CloseAction

    /**
     * Called after the gui has been closed, both by the component gui, or by other actors, such as a teleport packet.
     */
    fun onAfterClose()
}