package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class SubCategory {
    @ConfigOption(
        name = "Test Opt",
        desc = "com"
    )
    @ConfigEditorBoolean
    public boolean w = false;
}
