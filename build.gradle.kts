plugins {
    idea
    java
    `maven-publish`
    id("xyz.wagyourtail.unimined") version "0.4.9"
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "io.github.moulberry"
version = "1.0.0"

minecraft {
    forge {
        it.mcpChannel = "stable"
        it.mcpVersion = "22-1.8.9"
        it.setDevFallbackNamespace("intermediary")
    }
    mcRemapper.tinyRemapperConf = {
        it.ignoreFieldDesc(true)
        it.ignoreConflicts(true)
    }
    launcher.config("client") {
        this.jvmArgs.add("-Dmoulconfig.testmod=true")
        this.args.add(0, "--tweakClass")
        this.args.add(1, "net.minecraftforge.fml.common.launcher.FMLTweaker")

    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.nea.moe/releases")
}

dependencies {
    minecraft("net.minecraft:minecraft:1.8.9")
    mappings("moe.nea.mcp:mcp-yarn:1.8.9")
    "forge"("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

sourceSets.main {
    output.setResourcesDir(file("$buildDir/classes/java/main"))
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}
project.afterEvaluate {
    tasks.named("runClient", JavaExec::class) {
        this.javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    }
}

license {
    header(project.file("HEADER.txt"))
    properties {
        set("year", 2023)
    }
    skipExistingHeaders(true)
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.remapJar) {
                classifier = ""
            }
            artifact(tasks.jar) {
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
}