package io.github.notenoughupdates.moulconfig.test

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDraggableList
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorInfoText
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorText
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import io.github.notenoughupdates.moulconfig.observer.Property
import org.lwjgl.glfw.GLFW
import java.util.Arrays

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


    @Accordion
    @ConfigOption(name = "Accordion", desc = "")
    var accordion = AccordionClass()

    class AccordionClass() {

        @ConfigOption(name = "Number Dropdown", desc = "0, 1, 2, 3")
        @ConfigEditorDropdown(values = ["0", "1", "2", "3"])
        var numberDropdown: Int = 0


        @ConfigOption(name = "Enum Dropdown", desc = "1, 2, 3, 4")
        @ConfigEditorDropdown
        var enumDropdown: Property<DropdownEnum> = Property.of(DropdownEnum.FOUR)
    }

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

    @ConfigOption(name = "Draggable List", desc = "§eDrag text to change the order of the list.")
    @ConfigEditorDraggableList(
        exampleText = ["abc", "dec", "blah", "surel it works really cool and great :))"]
    )
    var draggableList: List<Int> = ArrayList(mutableListOf(0, 1, 2, 3))

    @Expose
    @ConfigOption(name = "Enum Draggable List", desc = "Draggable list but doesnt work properly.")
    @ConfigEditorDraggableList
    var enumDraggableList: List<EnumDraggableList> = ArrayList(
        Arrays.asList(
            EnumDraggableList.ONE,
            EnumDraggableList.THREE,
            EnumDraggableList.TWO,
        )
    )

    enum class EnumDraggableList(private val str: String) {
        ONE("1"),
        TWO("too"),
        THREE("three"),
        ;

        override fun toString(): String {
            return str
        }
    }

    @Expose
    @ConfigOption(name = "Keybind", desc = "The Number One")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_1)
    var slot1: Int = GLFW.GLFW_KEY_1
}
