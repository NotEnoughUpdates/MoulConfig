package io.github.notenoughupdates.moulconfig.test;

import io.github.notenoughupdates.moulconfig.annotations.Category;

public class TestCategoryB {
    @Category(name = "Test Inner Category", desc = "")
    public InnerCategory innerCategory = new InnerCategory();
}
