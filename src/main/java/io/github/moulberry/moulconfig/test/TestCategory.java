package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.annotations.*;

public class TestCategory {
    @ConfigOption(name = "Text Test", desc = "Text Editor Test")
    @ConfigEditorText
    public String text = "Text";
    @ConfigOption(name = "Another Text test", desc = "Text Editor Test")
    @ConfigEditorText
    public String text2 = "Text 2";
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

}
