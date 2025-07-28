package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.common.text.StructuredText

class TestConfig : Config() {
    override fun getTitle(): StructuredText {
        return StructuredText.of("1.20 Test").green()
    }

    override fun isValidRunnable(runnableId: Int): Boolean {
        return false
    }
    @Category(name = "Cat a", desc = "Cat a desc")
    var testCategoryA: TestCategoryA = TestCategoryA()
}
