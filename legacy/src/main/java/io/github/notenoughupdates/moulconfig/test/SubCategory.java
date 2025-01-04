package io.github.notenoughupdates.moulconfig.test;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigAccordionId;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorAccordion;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class SubCategory {
    @ConfigEditorAccordion(id = 1)
    @Expose
    @ConfigOption(
        name = "Test Opt",
        desc = "com"
    )
    private boolean testOption = false;
    @ConfigAccordionId(id = 1)
    @Expose
    @ConfigOption(
        name = "EICAR",
        desc = "com"
    )
    @ConfigEditorBoolean
    public boolean we = false;
}
