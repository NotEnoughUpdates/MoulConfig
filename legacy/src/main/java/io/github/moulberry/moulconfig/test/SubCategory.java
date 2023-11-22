package io.github.moulberry.moulconfig.test;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class SubCategory {
    @Expose
    @ConfigOption(
        name = "Test Opt",
        desc = "com"
    )
    @ConfigEditorBoolean
    public boolean w = false;
    @Expose
    @ConfigOption(
        name = "EICAR",
        desc = "com"
    )
    @ConfigEditorBoolean
    public boolean we = false;
}
