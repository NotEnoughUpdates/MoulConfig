package io.github.moulberry.moulconfig.common

data class MyResourceLocation(val root: String, val path: String) {
    init {
        require(":" !in root)
        require(":" !in path)
    }
}