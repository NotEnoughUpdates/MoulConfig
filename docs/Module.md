# Module MoulConfig

MoulConfig is a mod configuration GUI for Minecraft™ mods.

It was originally developed by [Moulberry](https://moulberry.codes),
and is now mostly maintained by nea89. The original code was available
under an LGPL 3.0 license, and this version keeps that license.

## Installation

This version of MoulConfig is published to the [NEU Maven Repository][neurepo]. The latest
version number can be [found there as well][versionlisting].

There are two relevant artifacts. The test mod, which should be used during development that provides textures, and
the library jar, which should be shaded and compiled against (it also contains textures, but forge cannot load those
textures). Below is an example on how to use this with architectury loom. Not all of this example might be necessary
for your use case, but this should take you relatively far.

```kotlin

repositories {
    /// Your other releases
    maven("https://maven.notenoughupdates.org/releases/")
}

val devenvMod by configurations.creating {
    isTransitive = false
    isVisible = false
}

// Your gradle template probably already includes something like this configuration.
// **Make sure that the configuration is extending modImplementation, otherwise you will run into name issues**
val shadowModImpl by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    // Where shadowModImpl is a gradle configuration that remaps and shades the jar.
    // This version should not have :test at the end (so that you don't accidentally reference the test mod)
    "shadowModImpl"("org.notenoughupdates.moulconfig:MoulConfig:<version>")

    // Where devenvMod is a gradle configuration from which all resolved jars are loaded as mod.
    // Be aware of the :test at the end.
    "devenvMod"("org.notenoughupdates.moulconfig:MoulConfig:<version>:test")
}

// The code below is just to demonstrate how a gradle configuration might be loaded as a list of development environment
// mods without user interaction
loom {
    launchConfigs {
        "client" {
            arg("--mods", devenvMod.resolve().joinToString(",") { it.relativeTo(file("run")).path })
        }
    }
}


tasks.shadowJar {
    // Make sure to relocate MoulConfig to avoid version clashes with other mods
    configurations = listOf(shadowModImpl)
    relocate("io.github.notenoughupdates.moulconfig", "my.mod.deps.moulconfig")
}
```

## Usage

See the [TestMod](io.github.notenoughupdates.moulconfig.test.MoulConfigTest) for usage examples, or check out the annotation
package documentation to see the kind of config variable editors MoulConfig has built in.

[neurepo]: https://maven.notenoughupdates.org/#/

[versionlisting]: https://maven.notenoughupdates.org/#/releases/org/notenoughupdates/moulconfig/MoulConfig
