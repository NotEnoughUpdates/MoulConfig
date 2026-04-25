package io.github.notenoughupdates.moulconfig.test;

import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;

public class TestConfig extends Config {
    @Override
    public StructuredText getTitle() {
        return StructuredText.of("1.20 Test").green();
    }

    @Override
    public boolean isValidRunnable(int runnableId) {
        return false;
    }

    @Category(name = "Cat a", desc = "Cat a desc")
    public TestCategoryA testCategoryA = new TestCategoryA();

    @Category(name = "Cat b", desc = "Cat b desc")
    public TestCategoryB testCategoryB = new TestCategoryB();

    public TestCategoryA getTestCategoryA() {
        return testCategoryA;
    }

    public TestCategoryB getTestCategoryB() {
        return testCategoryB;
    }
}
