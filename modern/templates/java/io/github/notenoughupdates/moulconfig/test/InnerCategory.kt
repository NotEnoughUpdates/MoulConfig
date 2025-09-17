package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class InnerCategory {

    @ConfigEditorBoolean
    @ConfigOption(name = "Test Option", desc = "Test toggle")
    var shouldTestToggle: Boolean = false

}
