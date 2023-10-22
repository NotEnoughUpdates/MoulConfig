plugins {
    java
    kotlin("jvm")
    id("gg.essential.loom")
    id("com.github.johnrengelman.shadow")
    `maven-publish`
}

loom {
    launches {
        named("client") {
            arg("--accessToken", "{}")
            arg("--version", "1.20")
        }
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

val shadowInclude by configurations.creating
dependencies {
    "minecraft"("com.mojang:minecraft:1.20")
    mappings("net.fabricmc:yarn:1.20+build.1")
    implementation(project(":common"))
    modImplementation("net.fabricmc:fabric-loader:0.14.22")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.0+1.20")
    shadowInclude(project(":common", configuration = "singleFile"))
}

tasks.shadowJar {
    configurations = listOf(shadowInclude)
    archiveClassifier.set("dev")
}

tasks.remapJar {
    archiveClassifier.set("")
    input.set(tasks.shadowJar.flatMap { it.archiveFile })
}

tasks.jar {
    archiveClassifier.set("small")
    dependsOn(tasks.processResources)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.remapJar) {
                classifier = ""
            }
            artifact(tasks.shadowJar) {
                classifier = "named"
            }
        }
    }
}



