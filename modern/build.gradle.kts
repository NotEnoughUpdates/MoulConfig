plugins {
    java
    kotlin("jvm")
    id("xyz.wagyourtail.unimined") version "1.0.5"
}

unimined.minecraft {
    version("1.20")
    mappings {
        intermediary()
        yarn("1")
    }
    fabric {
        loader("0.14.20")
    }
}
dependencies {
    implementation(project(":common"))
}

