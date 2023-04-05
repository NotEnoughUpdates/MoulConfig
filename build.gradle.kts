import java.io.ByteArrayOutputStream

plugins {
    idea
    java
    `maven-publish`
    id("xyz.wagyourtail.unimined") version "0.4.9"
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "org.notenoughupdates.moulconfig"

fun cmd(vararg args: String): String? {
    val output = ByteArrayOutputStream()
    val r = exec {
        this.commandLine(args.toList())
        this.isIgnoreExitValue = true
        this.standardOutput = output
        this.errorOutput = ByteArrayOutputStream()
    }
    return if (r.exitValue == 0) output.toByteArray().decodeToString().trim()
    else null
}

val tag = cmd("git", "describe", "--tags", "HEAD")
val hash = cmd("git", "rev-parse", "--short", "HEAD")!!
val isSnapshot = tag == null
version = tag ?: hash

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

    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
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

val noTestJar by tasks.creating(Jar::class) {
    from(zipTree(tasks.remapJar.map { it.archiveFile }))
    archiveClassifier.set("notest")
    exclude("io/github/moulberry/moulconfig/test/*")
    exclude("mcmod.info")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(noTestJar) {
                classifier = ""
            }
            artifact(tasks.remapJar) {
                classifier = "test"
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
