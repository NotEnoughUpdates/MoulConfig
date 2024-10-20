package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.annotations.*
import io.github.notenoughupdates.moulconfig.observer.Property

class TestCategoryA {
    @ConfigOption(name = "Test Option", desc = "Test toggle")
    @ConfigEditorBoolean
    var shouldTestToggle: Boolean = false

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

    @ConfigOption(name = "Slider", desc = "Between 1 and 5")
    @ConfigEditorSlider(minValue = 1F, maxValue = 5f, minStep = 1F)
    var minimumTitle: Int = 1

    @ConfigOption(name = "Info Box", desc = "Shows important info to the user")
    @ConfigEditorInfoText(
        infoTitle = "§cAlert",
    )
    var notice: String = ""

    @ConfigOption(name = "Text Box", desc = "Lets you put strings.")
    @ConfigEditorText
    var customText: Property<String> = Property.of("abc")
}
