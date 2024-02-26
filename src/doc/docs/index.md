# MoulConfig

MoulConfig is a mod configuration GUI for Minecraft™ mods.

It was originally developed by [Moulberry](https://moulberry.codes),
and is now mostly maintained by [nea89](https://nea.moe/). The original code was available
under an LGPL 3.0 license, and this version keeps that license.

## Installation

This version of MoulConfig is published to the [NEU Maven Repository][neurepo]. The latest
version number can be [found there as well][versionlisting].

There are two versions of MoulConfig: `legacy` for 1.8.9 and `modern` for 1.20.2. Legacy has support for the config gui
and for the gui library, while modern only supports the gui library.

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

// Your gradle template probably already includes something like this configuration.
// **Make sure that the configuration is extending modImplementation, otherwise you will run into name issues**
val shadowModImpl by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    // Where shadowModImpl is a gradle configuration that remaps and shades the jar.
    "shadowModImpl"("org.notenoughupdates.moulconfig:legacy:<version>")
}

// This snippet is required in order to correctly load resources in the development environment
loom {
    launchConfigs {
        "client" {
            arg("--tweakClass", "io.github.notenoughupdates.moulconfig.tweaker.DevelopmentResourceTweaker")
        }
    }
}

tasks.shadowJar {
    // Make sure to relocate MoulConfig to avoid version clashes with other mods
    configurations = listOf(shadowModImpl)
    relocate("io.github.notenoughupdates.moulconfig", "my.mod.deps.moulconfig")
}
```

### Modern installation

Just shadow MoulConfig like any other mod. It is *highly* recommended that you relocate MoulConfig to another package,
since our internals do not obey any backwards compatibility guarantees. No other buildscript configuration is necessary.

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
    "shadowModImpl"("org.notenoughupdates.moulconfig:modern:<version>")
}

tasks.shadowJar {
    // Make sure to relocate MoulConfig to avoid version clashes with other mods
    configurations = listOf(shadowModImpl)
    relocate("io.github.notenoughupdates.moulconfig", "my.mod.deps.moulconfig")
}
```


## Usage

See
the [TestMod](https://github.com/NotEnoughUpdates/MoulConfig/blob/master/modern/src/main/kotlin/io/github/notenoughupdates/moulconfig/test)
for usage examples, or check out the annotation
package documentation to see the kind of config variable editors MoulConfig has built in.

[neurepo]: https://maven.notenoughupdates.org/#/

[versionlisting]: https://maven.notenoughupdates.org/#/releases/org/notenoughupdates/moulconfig/
