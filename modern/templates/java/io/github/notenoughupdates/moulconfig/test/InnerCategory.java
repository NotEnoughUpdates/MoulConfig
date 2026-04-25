package io.github.notenoughupdates.moulconfig.test;

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class InnerCategory {
    @ConfigEditorBoolean
    @ConfigOption(name = "Test Option", desc = "Test toggle")
    public boolean shouldTestToggle = false;
}
