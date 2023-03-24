plugins {
    idea
    java
    id("xyz.wagyourtail.unimined") version "0.4.1"
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
    launches.apply {
        setConfig("client") {
            this.args.add(0, "--tweakClass")
            this.args.add(1, "net.minecraftforge.fml.common.launcher.FMLTweaker")
        }
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
        set("name", "Linnea Gräf")
        set("year", 2023)
    }
    skipExistingHeaders(true)
}

