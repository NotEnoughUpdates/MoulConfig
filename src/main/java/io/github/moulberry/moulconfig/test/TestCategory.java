/*
 * Copyright (C) 2023 NotEnoughUpdates contributors
 *
 * This file is part of MoulConfig.
 *
 * MoulConfig is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * MoulConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MoulConfig. If not, see <https://www.gnu.org/licenses/>.
 *
 */

/**/
package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.Overlay;
import io.github.moulberry.moulconfig.annotations.*;
import io.github.moulberry.moulconfig.observer.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class TestCategory {

    @Accordion
    @ConfigOverlay
    @ConfigOption(name = "Overlay", desc = "Overlayering")
    public TestOverlay testOverlay = new TestOverlay();

    public static class TestOverlay extends Overlay {
        @Override
        public String getName() {
            return "Test overlay";
        }
    }

    @ConfigEditorButton(buttonText = "RUN!")
    @ConfigOption(name = "Button using runnable", desc = "Click to run")
    public Runnable doRun = () -> Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Hehehe"));
    @ConfigEditorButton(buttonText = "RUN!", runnableId = 10)
    @ConfigOption(name = "Button using runnableId", desc = "Click to run")
    public boolean whatEver;

    @ConfigEditorInfoText(infoTitle = "Test")
    @ConfigOption(name = "More Info text Test", desc = "Even more text to do shit with")
    public boolean infoText;


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
    @ConfigEditorAccordion(id = 1000)
    public boolean accordionOne = false;

    @ConfigOption(name = "Accordion", desc = "First accordion")
    @ConfigEditorAccordion(id = 1001)
    @ConfigAccordionId(id = 1000)
    public boolean accordionInner = false;

    @ConfigOption(name = "Color Picker", desc = "Color Picker test")
    @ConfigEditorColour
    @ConfigAccordionId(id = 1001)
    public String colour = "0:0:0:0:0";

    @ConfigOption(name = "Number", desc = "Slider test")
    @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
    @ConfigAccordionId(id = 1000)
    public int slider = 0;

    @ConfigOption(name = "Key Binding", desc = "Key Binding")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F)
    public int keyBoard = Keyboard.KEY_F;

    @Accordion
    @ConfigOption(name = "Hehe", desc = "hoho")
    public TestAcc testAcc = new TestAcc();

    public static class TestAcc {
        @ConfigOption(name = "a", desc = "b")
        @ConfigEditorColour
        public String colour = "0:0:0:0:0";
    }

    @ConfigOption(name = "Accordion", desc = "First accordion")
    @ConfigEditorAccordion(id = 1002)
    public boolean accordionTwo = false;

    @ConfigOption(name = "Drag List", desc = "Using integers")
    @ConfigEditorDraggableList(
        exampleText = {"A", "2", "3", "4"}
    )
    public List<Integer> thingy = new ArrayList<>();

    @ConfigOption(name = "Drag List 2", desc = "Using EnumSet")
    @ConfigEditorDraggableList(requireNonEmpty = true)
    public List<TestEnum> draggableEnum = new ArrayList<>();

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
