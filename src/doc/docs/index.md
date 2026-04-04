# MoulConfig

MoulConfig is a mod configuration GUI for Minecraft™ mods.

It was originally developed by [Moulberry](https://moulberry.codes),
and is now mostly maintained by [nea89](https://nea.moe/). The original code was available
under an LGPL 3.0 license, and this version keeps that license.

## Installation

This version of MoulConfig is published to the [NEU Maven Repository][neurepo]. The latest
version number can be [found there as well][versionlisting].

MoulConfig is split into two parts:

- `common`, which contains logic for rendering, annotation processing, and most of the code you will interact with
- `platform`, which contains code for coupling rendering and input with one specific platform
    - `modern-<version>`, which contains compatibility code for modern minecraft versions (1.20+)
    - `legacy`, which contains 1.8.9 compatibility code

### Modern installation

Just shadow MoulConfig like any other mod. It is *highly* recommended that you relocate MoulConfig to another package,
since our internals do not obey any backwards compatibility guarantees. No other buildscript configuration is necessary.

Each minecraft version has its own `modern-<minecraftVersion>` module. Those modules automatically contain the corresponding
`common` code for the relevant MoulConfig version.

You will also need to copy the relevant `moulconfig.accesswidener` to your own. Check the relevant source directory for
used accesswideners. In the future there might be a plugin to automatically shade / combine accesswideners.

```kt
repositories {
    maven("https://maven.notenoughupdates.org/releases/")
}

// Your gradle template probably already includes something like this configuration.
// **Make sure that the configuration is extending modImplementation, otherwise you will run into name issues**
val shadowModImpl by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    // Where shadowModImpl is a gradle configuration that remaps and shades the jar.
    "shadowModImpl"("org.notenoughupdates.moulconfig:modern-<mcversion>:<version>")
}

tasks.shadowJar {
    // Make sure to relocate MoulConfig to avoid version clashes with other mods
    configurations = listOf(shadowModImpl)
    relocate("io.github.notenoughupdates.moulconfig", "my.mod.deps.moulconfig")
}
```

### Legacy installation

On 1.8.9 Forge you will need to shadow MoulConfig like any other mod dependency. It is *highly* recommended that
you relocate MoulConfig to another package, since our internals do not obey any backwards compatibility guarantees.
In order to get resources loaded in your development environment is is recommended that you also make use of the
`io.github.notenoughupdates.moulconfig.tweaker.DevelopmentResourceTweaker`. Do not specify this tweaker in your JAR manifest,
*only* as a command line argument during development time. See a snippet on how to use MoulConfig with architectury loom
below:

```kotlin

repositories {
    maven("https://maven.notenoughupdates.org/releases/")
}

// Your Gradle template probably already includes something like this configuration.
// **Make sure that the configuration is extending modImplementation, otherwise you will run into name issues**
val shadowModImpl by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    // Where shadowModImpl is a gradle configuration that remaps and shades the jar.
    "shadowModImpl"("org.notenoughupdates.moulconfig:legacy:<version>")
}

tasks.shadowJar {
    // Make sure to relocate MoulConfig to avoid version clashes with other mods
    configurations = listOf(shadowModImpl)
    relocate("io.github.notenoughupdates.moulconfig", "my.mod.deps.moulconfig")
}
```


## Usage

See
the [FabricMain](https://github.com/NotEnoughUpdates/MoulConfig/blob/v4/modern/templates/java/io/github/notenoughupdates/moulconfig/test/FabricMain.java)
for usage examples, or check out the annotation
package documentation to see the kind of config variable editors MoulConfig has built in.

[neurepo]: https://maven.notenoughupdates.org/#/

[versionlisting]: https://maven.notenoughupdates.org/#/releases/org/notenoughupdates/moulconfig/
