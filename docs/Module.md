# Module MoulConfig

MoulConfig is a mod configuration GUI for Minecraft™ mods.

It was originally developed by [Moulberry](https://moulberry.codes),
and is mostly maintained by nea89. The original code was available
under an LGPL 3.0 license, and this version keeps that license.

This version of the mod was changed to support 1.20 fabric

## Installation

This version of MoulConfig is published to the [My Personal Repository][morasrepo]. The latest
version number can be [found there as well][versionlisting].

There are two relevant artifacts. The test mod, which should be used during development that provides textures, and
the library jar, which should be shaded and compiled against (it also contains textures, but forge cannot load those
textures).

```groovy
repositories {
    maven("https://repo.morazzer.dev/releases/")
}

val shadowImplementation by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    shadowImplementation("dev.morazzer:moulconfig:<version>") {
        is
    }
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(shadowImplementation)
    relocate("io.github.moulberry.moulconfig", "my.mod.deps.moulconfig")
}
```

## Usage

See the [TestMod](io.github.moulberry.moulconfig.test.MoulConfigTest) for usage examples, or check out the annotation
package documentation to see the kind of config variable editors MoulConfig has built in.

[morasrepo]: https://repo.morazzer.dev/#/

[versionlisting]: https://repo.morazzer.dev/#/releases/dev/morazzer/moulconfig
