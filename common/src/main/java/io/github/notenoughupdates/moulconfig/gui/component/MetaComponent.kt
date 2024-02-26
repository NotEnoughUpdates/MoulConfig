package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.gui.CloseEventListener
import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.GuiContext
import io.github.notenoughupdates.moulconfig.gui.GuiImmediateContext
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import java.util.function.Supplier

/**
 * Component providing XML wrappers and such the ability to wrap meta operations that operate on the entire screen.
 * This component should be permanently mounted and does not impact layouting or rendering.
 */
class MetaComponent(
    val beforeClose: Supplier<CloseEventListener.CloseAction>? = null,
    val afterClose: Runnable? = null,
    val requestClose: GetSetter<Runnable>? = null
) : GuiComponent(), CloseEventListener {
    override fun setContext(context: GuiContext?) {
        super.setContext(context)
        requestClose?.set(Runnable {
            context?.requestClose()
        })
    }

    override fun onBeforeClose(): CloseEventListener.CloseAction {
        return beforeClose?.get() ?: CloseEventListener.CloseAction.NO_OBJECTIONS_TO_CLOSE
    }

    override fun onAfterClose() {
        afterClose?.run()
    }

    override fun getWidth(): Int {
        return 0
    }

    override fun getHeight(): Int {
        return 0
    }

    override fun render(context: GuiImmediateContext) {
    }
}