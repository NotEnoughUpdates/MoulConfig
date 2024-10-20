package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import io.github.notenoughupdates.moulconfig.observer.Property

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


    @ConfigOption(name = "Number Dropdown", desc = "0, 1, 2, 3")
    @ConfigEditorDropdown(values = ["0", "1", "2", "3"])
    var numberDropdown: Int = 0


    @ConfigOption(name = "Enum Dropdown", desc = "1, 2, 3, 4")
    @ConfigEditorDropdown
    var enumDropdown: Property<DropdownEnum> = Property.of(DropdownEnum.FOUR)

    enum class DropdownEnum(private val label: String) {
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        ;
        override fun toString(): String {
            return label
        }
    }
}
