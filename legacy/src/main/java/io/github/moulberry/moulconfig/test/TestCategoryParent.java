package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.annotations.Category;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class TestCategoryParent {
    @Category(name = "SubCategory", desc = "Subbier Sub description")
    public SubCategory subCategory = new SubCategory();
    @ConfigOption(
        name = "Test Opt Parent",
        desc = "com111"
    )
    @ConfigEditorBoolean
    public boolean w = false;
}
