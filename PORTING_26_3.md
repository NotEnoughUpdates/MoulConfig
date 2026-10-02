# Building and publishing MoulConfig for Minecraft 26.3

This source adds the real `:modern:modern-26.3` platform beside the existing versions. Common code retains its original Java 8 compiler/ABI; the native 26.3 platform uses Java 25. Both JDKs must be installed in caller-selected directories. Use Java 25 to run the committed Gradle 9.4.0 wrapper and point Gradle toolchain discovery at both directories. Compiling common sources with Java 25 `--release 8` is not equivalent here: the original Manifold/Lombok compilation needs the genuine Java 8 compiler.

The port source is based on [NotEnoughUpdates/MoulConfig v4 commit bd7b9aa3dbf7cef428280daf0156cb5e06338530](https://github.com/NotEnoughUpdates/MoulConfig/tree/bd7b9aa3dbf7cef428280daf0156cb5e06338530), with the reviewed 26.3 source changes applied. The LGPL 3.0-or-later source/license headers remain present. The build's existing `moulconfig.portVersion` **JVM system property** supplies a release version for source archives without Git tags. Use `-Dmoulconfig.portVersion=4.7.2-codex26.3.1` to reproduce the tested coordinate; do not use `-P` for this version override. The source classes and their functional metadata remain unchanged.

`-PportDependencyRepository=...` adds an optional local Maven publication destination named `portDependencies`. A relative path resolves against the source root; an absolute external directory or file URI avoids source-tree output. Without that argument, existing publication defaults remain unchanged. The exact task below publishes only to the explicitly selected directory; it does not invoke an upstream remote publication task or require publisher credentials.

Choose `JDK8`, `JDK25` and `PORT_DEPENDENCIES` outside the source checkout, then run from this source root:

```sh
export JAVA_HOME="$JDK25"
./gradlew --no-daemon --max-workers=4 --configure-on-demand \
  :modern:modern-26.3:remapJar \
  :modern:modern-26.3:check \
  :modern:modern-26.3:publishMavenPublicationToPortDependenciesRepository \
  -Dmoulconfig.portVersion=4.7.2-codex26.3.1 \
  -Porg.gradle.java.installations.paths="$JDK8,$JDK25" \
  -PportDependencyRepository="$PORT_DEPENDENCIES"
```

The runtime library is published as:

```text
org.notenoughupdates.moulconfig:modern-26.3:4.7.2-codex26.3.1
<PORT_DEPENDENCIES>/org/notenoughupdates/moulconfig/modern-26.3/4.7.2-codex26.3.1/modern-26.3-4.7.2-codex26.3.1.jar
```

The same publication also includes the real generated POM, named development JAR and sources JAR. Consumers should resolve the unclassified production library from this explicit repository. The previously reviewed production candidate SHA256 is `eebfbf6ff1cfacc117f47b75877b1a5df4c239204633dbd23bc68b8c76722ce0`. Treat it as the reference build identity; verify rebuilt payloads and record the selected JDK/dependency versions rather than assume arbitrary JDKs or future SNAPSHOT dependencies produce identical ZIP bytes. The declared upstream Unimined dependency remains a SNAPSHOT, so retaining the exact resolved build is necessary for strict binary reproduction.

No owner configuration, environment/login files, DevAuth client or game launch is needed. Only the requested native platform and common source dependencies need configuration; `--configure-on-demand` avoids evaluating unrelated platforms. These Gradle checks establish build/publication behavior and do not replace rendered GUI/input tests or consumer/full-pack runtime tests. The 26.3 keybind backend uses native SDL keyboard values and encoded logical mouse values; a consumer migrating an older numeric configuration must apply its reviewed schema conversion separately.
