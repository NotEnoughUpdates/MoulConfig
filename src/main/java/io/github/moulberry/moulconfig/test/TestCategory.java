package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.annotations.*;
import io.github.moulberry.moulconfig.observer.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TestCategory {
    @ConfigEditorButton(buttonText = "RUN!")
    @ConfigOption(name = "Button using runnable", desc = "Click to run")
    public Runnable doRun = () -> Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Hehehe"));
    @ConfigEditorButton(buttonText = "RUN!", runnableId = 10)
    @ConfigOption(name = "Button using runnableId", desc = "Click to run")
    public boolean whatEver;


    @ConfigOption(name = "Text Test", desc = "Text Editor Test")
    @ConfigEditorText
    public String text = "Text";

    @ConfigOption(name = "Another Text test", desc = "Text Editor Test")
    @ConfigEditorText
    public Property<String> text2 = Property.of("Text 2");

    @ConfigOption(name = "Boolean test", desc = "Toggle test")
    @ConfigEditorBoolean
    public boolean bool = true;
    @ConfigOption(name = "Accordion", desc = "First accordion")
    @ConfigEditorAccordion(id = 0)
    public boolean accordionOne = false;

    @ConfigOption(name = "Accordion", desc = "First accordion")
    @ConfigEditorAccordion(id = 1)
    @ConfigAccordionId(id = 0)
    public boolean accordionInner = false;

    @ConfigOption(name = "Color Picker", desc = "Color Picker test")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1)
    public String colour = "0:0:0:0:0";

    @ConfigOption(name = "Number", desc = "Slider test")
    @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
    @ConfigAccordionId(id = 0)
    public int slider = 0;

    @ConfigOption(name = "Accordion", desc = "First accordion")
    @ConfigEditorAccordion(id = 2)
    public boolean accordionTwo = false;

    @ConfigOption(name = "Drag List", desc = "Using integers")
    @ConfigEditorDraggableList(
        exampleText = {"A", "2", "3", "4"}
    )
    public List<Integer> thingy = new ArrayList<>();

    @ConfigOption(name = "Drag List 2", desc = "Using EnumSet")
    @ConfigEditorDraggableList()
    public EnumSet<TestEnum> draggableEnum = EnumSet.of(TestEnum.C);

    @ConfigOption(name = "Drop Down", desc = "Using integer")
    @ConfigEditorDropdown(
        values = {"A", "B", "C"}
    )
    public int dropDownUsingIntegers = 0;
    @ConfigOption(name = "Drop Down", desc = "Using strings")
    @ConfigEditorDropdown(
        values = {"A", "B", "C"}
    )
    public String dropDownUisngStrings = "A";

    @ConfigOption(name = "Drop Down", desc = "Using enums")
    @ConfigEditorDropdown()
    public TestEnum dropDownUsingEnum = TestEnum.A;

    enum TestEnum {
        A, B, C, D, E;

        @Override
        public String toString() {
            return "Enum Value " + name();
        }
    }

    @Accordion
    @ConfigOption(name = "Test Accordion", desc = "With class")
    public TestAccordion testAccordion = new TestAccordion();

    public static class TestAccordion {

        @ConfigOption(name = "Sub ACcordion", desc = "Hehehe")
        @Accordion
        public SubAccordion subAccordion = new SubAccordion();
    }

    public static class SubAccordion {
        @ConfigOption(name = "Test Value", desc = "AAAA")
        @ConfigEditorButton
        public Runnable runValue = () -> Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Hehehe"));
    }


}
