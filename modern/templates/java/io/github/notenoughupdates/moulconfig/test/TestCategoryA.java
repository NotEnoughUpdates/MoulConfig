package io.github.notenoughupdates.moulconfig.test;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.ChromaColour;
import io.github.notenoughupdates.moulconfig.annotations.Accordion;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDraggableList;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorInfoText;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorText;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOrder;
import io.github.notenoughupdates.moulconfig.observer.Property;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCategoryA {
    @Expose
    @ConfigOrder(Integer.MAX_VALUE)
    @ConfigOption(name = "Bottom option!", desc = "Declared at top, but should appear at the bottom by Order annotation.")
    @ConfigEditorInfoText(infoTitle = "Bottom option")
    public boolean bottomOption = false;

    @ConfigOption(name = "Open Wide", desc = "Use a wider config menu")
    @ConfigEditorBoolean
    public boolean isWide = false;

    public boolean isWide() {
        return isWide;
    }

    @ConfigOption(name = "Test Option", desc = "Test toggle")
    @ConfigEditorBoolean
    public boolean shouldTestToggle = false;

    @ConfigOption(name = "Pronouns in MoulConfig", desc = "Fuck It")
    @ConfigEditorDropdown
    public Pronouns dropdownTest = Pronouns.ITITS;

    public enum Pronouns {
        HEHIM("He/Him"), SHEHER("She/Her"), ITITS("It/Its"), THEYTHEM("They/Them"), USE_NAME("Use Name");
        private final String label;
        Pronouns(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    @Accordion
    @ConfigOption(name = "Accordion", desc = "")
    public AccordionClass accordion = new AccordionClass();

    public static class AccordionClass {
        @ConfigOption(name = "Number Dropdown", desc = "0, 1, 2, 3")
        @ConfigEditorDropdown(values = {"0", "1", "2", "3"})
        public int numberDropdown = 0;

        @Expose
        @ConfigOption(name = "Drop Down", desc = "Using strings")
        @ConfigEditorDropdown(values = {"A", "B", "C"})
        public String dropDownUisngStrings = "A";

        @ConfigOption(name = "Enum Dropdown", desc = "1, 2, 3, 4")
        @ConfigEditorDropdown
        public Property<DropdownEnum> enumDropdown = Property.of(DropdownEnum.FOUR);
    }

    public enum DropdownEnum {
        ONE("1"), TWO("2"), THREE("3"), FOUR("4");
        private final String label;
        DropdownEnum(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    @ConfigOption(name = "Slider", desc = "Between 1 and 5")
    @ConfigEditorSlider(minValue = 1F, maxValue = 5F, minStep = 1F)
    public int minimumTitle = 1;

    @ConfigOption(name = "Info Box", desc = "Shows important info to the user")
    @ConfigEditorInfoText(infoTitle = "\u00a7cAlert")
    public String notice = "";

    @ConfigOption(name = "Text Box", desc = "Lets you put strings.")
    @ConfigEditorText(forbidden = "\u00a7z")
    public Property<String> customText = Property.of("abc");

    @ConfigOption(name = "Draggable List", desc = "\u00a7eDrag text to change the order of the list.")
    @ConfigEditorDraggableList(exampleText = {"abc", "dec", "blah", "surel it works really cool and great :))"})
    public List<Integer> draggableList = new ArrayList<>(Arrays.asList(0, 1, 2, 3));

    @Expose
    @ConfigOption(name = "Enum Draggable List", desc = "Draggable list but doesnt work properly.")
    @ConfigEditorDraggableList
    public List<EnumDraggableList> enumDraggableList = new ArrayList<>(Arrays.asList(EnumDraggableList.ONE, EnumDraggableList.THREE, EnumDraggableList.TWO));

    public enum EnumDraggableList {
        ONE("1"), TWO("too"), THREE("three");
        private final String str;
        EnumDraggableList(String str) { this.str = str; }
        @Override public String toString() { return str; }
    }

    @ConfigOption(name = "Colour Test", desc = "Test a colour editor")
    @ConfigEditorColour
    public ChromaColour colour = new ChromaColour(0F, 1F, 1F, 0, 0xFF);

    @Expose
    @ConfigOption(name = "Keybind", desc = "The Number One")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_1)
    public int slot1 = GLFW.GLFW_KEY_1;

    @Expose
    @ConfigOption(name = "Test Runnable", desc = "Test a java.lang.Runnable")
    @ConfigEditorButton(buttonText = "Click me")
    public final Runnable runnable = () -> System.out.println("JRunnable working");

    @Expose
    @ConfigOption(name = "Test Runnable", desc = "Test a second java.lang.Runnable")
    @ConfigEditorButton(buttonText = "Click me")
    public final Runnable secondRunnable = () -> System.out.println("Second Runnable working");

    @Expose
    @ConfigOption(name = "Test Runnable", desc = "Test a (ignored) runnable using runnableId to emit a test warning")
    @ConfigEditorButton(runnableId = 10, buttonText = "Click me")
    public final Object runnableId = new Object();

    @Expose
    @ConfigOrder(-1)
    @ConfigOption(name = "Top option!", desc = "Declared at the bottom, floated to top by Order annotation.")
    @ConfigEditorInfoText(infoTitle = "Top option")
    public boolean topOption = false;
}
