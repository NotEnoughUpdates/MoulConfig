
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.tasks.DokkaBaseTask
import xyz.wagyourtail.unimined.api.UniminedExtension
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    id("xyz.wagyourtail.unimined")
    id("moulconfig.kotlin")
    id("moulconfig.leaf")
    id("moulconfig.manifold")
}

val fabricVersion = property("moulconfig.fabric") as String
val minecraftVersion = property("moulconfig.minecraft") as String
val isDeobfuscated = findProperty("moulconfig.deobfuscated") != null
val useResourceLoaderv1 = findProperty("moulconfig.rlv1") as String?
val aF = project.file("src/main/resources/moulconfig.accesswidener")
val hasAW = aF.exists()
the<UniminedExtension>().minecraft {
    version(minecraftVersion)
    if (!isDeobfuscated) {
        mappings {
            intermediary()
            mojmap()
            // unimined currently incorrectly renames 1.21.11 namespaces to official instead of above 1.21.11
            devNamespace("mojmap")
        }
    }

    fabric {
        loader("0.18.4")
        if (hasAW)
            accessWidener(aF)
    }
    mods {
        this.modImplementation {
            this.mixinRemap {
                this.enableBaseMixin()
            }
        }
    }
    runs {
        config("client") {
            jvmArgs("-Dmoulconfig.testmod=true")
            jvmArgs("-Dmoulconfig.warn.crash=false")
            parseEnvFile(rootProject.file(".env")).forEach { (name, value) ->
                environment(name, value)
            }
            parseEnvFile(file(".env")).forEach { (name, value) ->
                environment(name, value)
            }
        }
        config("server") {
            enabled = false
        }
    }
}

val numericMinecraftVersion = minecraftVersion.split("-").first().split(".")
    .map { it.toInt() }
    .let {
        if (it.size < 3)
            it + listOf(0)
        else if (it.size == 3)
            it
        else error("Unparsable minecraft version $minecraftVersion")
    }
    .reduce { a, b -> a * 100 + b }
println("Numeric version for $minecraftVersion is $numericMinecraftVersion")
val preProcessorArgs = the<PreProcessorArgs>()
preProcessorArgs.forDefaultCompilation {
    define("MC", numericMinecraftVersion)
    if (numericMinecraftVersion >= 12107)
        define("MC217", "true")
}

val fabricDeps = extensions.create("fabricDeps", FabricUtils::class, fabricVersion)

if (useResourceLoaderv1 != null) {
    fabricDeps.impl("fabric-resource-loader-v1")
} else {
    fabricDeps.impl("fabric-resource-loader-v0")
}

val remapJar by tasks.named("remapJar", RemapJarTask::class) {
    asJar {
        archiveClassifier.set("")
    }

    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

tasks.named("jar", Jar::class) {
    archiveClassifier.set("small")
    dependsOn(tasks.processResources)
}

tasks.processResources {
    from(project(":modern").file("templates/resources")) {
        filesMatching("fabric.mod.json") {
            filter {
                if (!it.contains("accessWidener") || hasAW)
                    it
                else
                    ""
            }
        }
    }
}
val fSourceDest = layout.buildDirectory.dir("sharedModernSource")
val generateFilteredSource =
    if (project.hasProperty("moulconfig.symlinkSharedSources"))
        tasks.register("generateFilteredSourc", SymlinkTask::class) {
            from = project(":modern").file("templates/java")
            into = fSourceDest
        }
    else
        tasks.register("generateFilteredSource", Copy::class) {
            doFirst {
                if (fSourceDest.get().asFile.isFile)
                    fSourceDest.get().asFile.delete()
            }
            from(project(":modern").file("templates/java"))
            rootSpec.into(fSourceDest)
        }

sourceSets.main {
    java {
        srcDir(files(generateFilteredSource))
    }
}

tasks.withType(Jar::class) {
    this.filesMatching(listOf("fabric.mod.json")) {
        filter {
            if (it.contains("FabricMain")) ""
            else it
        }
    }
    exclude("io/github/notenoughupdates/moulconfig/test/**")
}

configure<PublishingExtension> {
    publications {
        defaultMaven {
            artifact(remapJar) {
                classifier = ""
            }
        }
    }
}

val thisProj = project
val modernProj = project(":modern")
modernProj.tasks.withType<DokkaBaseTask> {
    dependsOn(thisProj.tasks.compileJava)
}
modernProj.configure<DokkaExtension> {
    this.dokkaSourceSets {
        this.register(thisProj.name) {
//                if (false && !thisProj.name.contains("26")) {
//                    val modernId = project.objects.newInstance(SourceSetIdSpec::class, "modern", "modern-26.1")
//                    this.dependentSourceSets.add(modernId)
//                }
            this.sourceSetScope.set("modern")
            this.classpath.from(thisProj.sourceSets.main.map { it.compileClasspath })
            this.displayName.set(minecraftVersion)
            this.sourceRoots.setFrom(
                listOf(
                    preProcessorArgs.preprocessedSources
                )
            )
            this.sourceLink {
                this.localDirectory.set(preProcessorArgs.preprocessedSources)
                this.remoteUrl("https://github.com/NotEnoughUpdates/MoulConfig/blob/${Version.hash}/modern/templates/java")
                this.remoteLineSuffix.set("#L")
            }
        }
    }
}