package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class TestCategoryA {
    @ConfigOption(name = "Test Option", desc = "Test toggle")
    @ConfigEditorBoolean
    var shouldTestToggle: Boolean = false
}
