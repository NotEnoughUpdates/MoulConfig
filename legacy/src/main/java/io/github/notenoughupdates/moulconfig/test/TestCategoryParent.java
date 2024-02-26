package io.github.notenoughupdates.moulconfig.test;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import io.github.notenoughupdates.moulconfig.annotations.SearchTag;

public class TestCategoryParent {
    @Category(name = "SubCategory", desc = "Subbier Sub description")
    @Expose
    public SubCategory subCategory = new SubCategory();
    @Expose
    @ConfigOption(
        name = "Test Opt Parent",
        desc = "com111"
    )
    @ConfigEditorBoolean
    @SearchTag("oomf")
    @SearchTag("hannibal2")
    public boolean w = false;
}
