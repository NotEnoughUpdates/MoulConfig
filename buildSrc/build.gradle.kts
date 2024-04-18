
plugins {
    `kotlin-dsl`
    kotlin("jvm")version "1.8.10"
}
repositories {
    mavenCentral()
}

dependencies {
}

sourceSets.main {
    kotlin {
        srcDir(file("src"))
    }
}