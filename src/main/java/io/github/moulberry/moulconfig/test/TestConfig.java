package io.github.moulberry.moulconfig.test;

import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.Category;

public class TestConfig extends Config {
    @Category(name = "Test Category", desc = "Test Description")
    public TestCategory testCategory = new TestCategory();
}
