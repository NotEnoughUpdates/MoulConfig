package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigEditorText;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

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
}
