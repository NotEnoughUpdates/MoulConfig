package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.annotations.Category

class TestConfig : Config() {
    override fun getTitle(): String {
        return "1.20 Test"
    }

    override fun isValidRunnable(runnableId: Int): Boolean {
        return false
    }
    @Category(name = "Cat a", desc = "Cat a desc")
    var testCategoryA: TestCategoryA = TestCategoryA()
}
