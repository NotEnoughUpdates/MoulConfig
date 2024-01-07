package io.github.moulberry.moulconfig.test;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.ConfigAccordionId;
import io.github.moulberry.moulconfig.annotations.ConfigEditorAccordion;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class SubCategory {
    @ConfigEditorAccordion(id = 1)
    @Expose
    @ConfigOption(
        name = "Test Opt",
        desc = "com"
    )
    public boolean w = false;
    @ConfigAccordionId(id = 1)
    @Expose
    @ConfigOption(
        name = "EICAR",
        desc = "com"
    )
    @ConfigEditorBoolean
    public boolean we = false;
}
