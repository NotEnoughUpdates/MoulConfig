package io.github.notenoughupdates.moulconfig.gui.component

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import java.util.function.Supplier

class WhenComponent(
    val condition: Supplier<out Boolean>,
    val ifTrue: Supplier<out GuiComponent>,
    val ifFalse: Supplier<out GuiComponent>,
) : IndirectComponent(
    Supplier {
        if (condition.get())
            ifTrue.get()
        else
            ifFalse.get()
    }
)