plugins {
    idea
    java
    `maven-publish`
    kotlin("jvm")
    id("gg.essential.loom")
    id("org.jetbrains.dokka")
    id("com.github.johnrengelman.shadow")
}

loom {
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))
dependencies {
    "minecraft"("com.mojang:minecraft:1.8.9")
    "mappings"("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    "forge"("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")
}
val include by configurations.creating {
    isVisible = true
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("org.jetbrains:annotations:24.0.1")
    implementation((project(":common")))
    include(project(":common", configuration = "singleFile"))
}

sourceSets.main {
    output.setResourcesDir(java.classesDirectory)
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    configurations = listOf(include)
}

tasks.dokkaHtml {
    dokkaSourceSets {
        ("main") {
            moduleName.set("MoulConfig-Forge")
            sourceRoots.from(sourceSets.main.get().allSource)
            classpath.from(tasks.compileJava.get().classpath)

            includes.from(fileTree("docs") { include("*.md") })
        }
    }
}

project.afterEvaluate {
    tasks.named("runClient", JavaExec::class) {
        this.javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(8))
        })
    }
}

tasks.jar {
    archiveClassifier.set("small")
}
tasks.shadowJar {
    archiveClassifier.set("dev")
}
tasks.remapJar {
    archiveClassifier.set("")
    dependsOn(tasks.shadowJar)
    input.set(tasks.shadowJar.flatMap { it.archiveFile })
}

val libraryJar by tasks.creating(Jar::class) {
    from(zipTree(tasks.remapJar.get().archiveFile))
    archiveClassifier.set("notest")
    exclude("io/github/moulberry/moulconfig/test/*")
    exclude("mcmod.info")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(libraryJar) {
                classifier = ""
            }
            artifact(tasks.remapJar) {
                classifier = "test"
            }
            artifact(tasks.shadowJar) {
                classifier = "named"
            }
            pom {
                licenses {
                    license {
                        name.set("LGPL-3.0 or later")
                        url.set("https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/HEAD/COPYING.LESSER")
                    }
                }
                developers {
                    developer {
                        name.set("NotEnoughUpdates contributors")
                    }
                    developer {
                        name.set("Linnea Gräf")
                    }
                }
                scm {
                    url.set("https://github.com/NotEnoughUpdates/MoulConfig")
                }
            }
        }
    }
    repositories {
        if (project.hasProperty("moulconfigPassword")) {
            maven {
                url = uri("https://maven.notenoughupdates.org/releases")
                name = "moulconfig"
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}


