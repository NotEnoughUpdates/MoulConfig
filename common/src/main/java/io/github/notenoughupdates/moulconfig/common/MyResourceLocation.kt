package io.github.notenoughupdates.moulconfig.common

data class MyResourceLocation(val root: String, val path: String) {
    init {
        require(":" !in root)
        require(":" !in path)
    }

    companion object {
        fun parse(string: String): MyResourceLocation {
            val s = string.split(":")
            return when (s.size) {
                1 -> MyResourceLocation("minecraft", s[0])
                2 -> MyResourceLocation(s[0], s[1])
                else -> error("Resource location has to be in the format `namespace:path`, with `namespace:` being optional")
            }
        }
    }
}