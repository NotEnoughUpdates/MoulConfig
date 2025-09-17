package io.github.notenoughupdates.moulconfig.test

import io.github.notenoughupdates.moulconfig.annotations.Category

class TestCategoryB {

    @Category(name = "Test Inner Category", desc = "")
    var innerCategory: InnerCategory = InnerCategory()

}
