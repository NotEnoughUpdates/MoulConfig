package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class TestCategoryA {
    @ConfigOption(name = "Test Option", desc = "Test toggle")
    @ConfigEditorBoolean
    var shouldTestToggle: Boolean = false

    @ConfigOption(name = "Pronouns in MoulConfig", desc = "Fuck It")
    @ConfigEditorDropdown
    var dropdownTest: Pronouns = Pronouns.ITITS

    enum class Pronouns(val label: String) {
        HEHIM("He/Him"),
        SHEHER("She/Her"),
        ITITS("It/Its"),
        THEYTHEM("They/Them"),
        USE_NAME("Use Name"),
        ;

        override fun toString(): String {
            return label
        }
    }

}
