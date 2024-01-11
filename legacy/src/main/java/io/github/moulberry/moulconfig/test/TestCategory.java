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

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.ChromaColour;
import io.github.moulberry.moulconfig.Overlay;
import io.github.moulberry.moulconfig.annotations.*;
import io.github.moulberry.moulconfig.observer.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestCategory {

    @Accordion
    @Expose
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
    @Expose
    @ConfigOption(name = "Button using runnable", desc = "Click to run")
    public Runnable doRun = () -> Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Hehehe"));
    @ConfigEditorButton(buttonText = "RUN!", runnableId = 10)
    @Expose
    @ConfigOption(name = "Button using runnableId", desc = "Click to run")
    public boolean whatEver;

    @ConfigEditorInfoText(infoTitle = "Test")
    @ConfigOption(name = "More Info text Test", desc = "Even more text to do shit with")
    @Expose
    public boolean infoText;


    @ConfigOption(name = "Text Test", desc = "Text Editor Test")
    @ConfigEditorText
    @Expose
    public String text = "Text";

    @ConfigOption(name = "Another Text test", desc = "Text Editor Test")
    @ConfigEditorText
    @Expose
    public Property<String> text2 = Property.of("Text 2");

    @ConfigOption(name = "Boolean test", desc = "Ut iste voluptatibus qui fugiat maxime. Praesentium incidunt beatae voluptas voluptatem repudiandae. Et qui sed ea. Dolorem sit aspernatur assumenda optio sunt dolorum nemo. Quo eaque id minima enim consequatur excepturi blanditiis autem. Quae porro saepe doloribus iure optio nam quae et.")
    @ConfigEditorBoolean
    @Expose
    public boolean bool = true;
    @ConfigOption(name = "Accordion", desc = "First accordion")
    @ConfigEditorAccordion(id = 1000)
    @Expose
    public boolean accordionOne = false;

    @ConfigOption(name = "Accordion", desc = "First accordion")
    @ConfigEditorAccordion(id = 1001)
    @Expose
    @ConfigAccordionId(id = 1000)
    public boolean accordionInner = false;

    @ConfigOption(name = "Color Picker", desc = "Color Picker test")
    @ConfigEditorColour
    @Expose
    @ConfigAccordionId(id = 1001)
    public String colour = "0:0:0:0:0";

    @ConfigOption(name = "Color Picker ChromaColour", desc = "Color Picker test")
    @ConfigEditorColour
    @Expose
    @ConfigAccordionId(id = 1001)
    public ChromaColour chromaColour = ChromaColour.fromStaticRGB(255, 255, 255, 255);

    @ConfigOption(name = "Number", desc = "Slider test")
    @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
    @ConfigAccordionId(id = 1000)
    @Expose
    public int slider = 0;

    @ConfigOption(name = "Key Binding", desc = "Key Binding")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F)
    @Expose
    public int keyBoard = Keyboard.KEY_F;

    @Accordion
    @ConfigOption(name = "Hehe", desc = "hoho")
    @Expose
    public TestAcc testAcc = new TestAcc();

    public static class TestAcc {
        @ConfigOption(name = "a", desc = "b")
        @ConfigEditorColour
        @Expose
        public String colour = "0:0:0:0:0";
    }

    @Expose
    @ConfigOption(name = "Accordion", desc = "First accordion")
    @ConfigEditorAccordion(id = 1002)
    public boolean accordionTwo = false;

    @ConfigOption(name = "Drag List", desc = "Using integers")
    @Expose
    @ConfigEditorDraggableList(
        exampleText = {"A", "2", "3", "4"}
    )
    public List<Integer> thingy = new ArrayList<>(Arrays.asList(10));

    @ConfigOption(name = "Drag List 2", desc = "Using EnumSet")
    @Expose
    @ConfigEditorDraggableList(requireNonEmpty = true)
    public List<TestEnum> draggableEnum = new ArrayList<>();

    @ConfigOption(name = "Drop Down", desc = "Using integer")
    @Expose
    @ConfigEditorDropdown(
        values = {"A", "B", "C"}
    )
    public int dropDownUsingIntegers = 0;
    @Expose
    @ConfigOption(name = "Drop Down", desc = "Using strings")
    @ConfigEditorDropdown(
        values = {"A", "B", "C"}
    )
    public String dropDownUisngStrings = "A";

    @ConfigOption(name = "Drop Down", desc = "Using enums")
    @Expose
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
    @Expose
    @ConfigOption(name = "Test Accordion", desc = "With class")
    public TestAccordion testAccordion = new TestAccordion();

    public static class TestAccordion {

        @Expose
        @ConfigOption(name = "Sub ACcordion", desc = "Hehehe")
        @Accordion
        public SubAccordion subAccordion = new SubAccordion();
    }

    public static class SubAccordion {
        @ConfigOption(name = "Test Value", desc = "AAAA")
        @Expose
        @ConfigEditorButton
        public Runnable runValue = () -> Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Hehehe"));
    }

    @ConfigOption(name = "Fuck it", desc = "Pronouns in MoulConfig")
    @Expose
    @ConfigEditorDraggableList(requireNonEmpty = true)
    public List<Pronouns> pronouns = new ArrayList<>(Collections.singletonList(Pronouns.USE_NAME));

    public enum Pronouns {
        HEHIM("He/Him"),
        SHEHER("She/Her"),
        ITITS("It/Its"),
        THEYTHEM("They/Them"),
        USE_NAME("Use Name"),
        ;
        String label;

        Pronouns(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

}
