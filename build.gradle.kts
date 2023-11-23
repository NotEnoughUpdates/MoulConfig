import org.jetbrains.dokka.gradle.DokkaTask
import java.io.ByteArrayOutputStream
import java.net.URL

plugins {
    alias(libs.plugins.dokka)
}


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
allprojects {
    group = "org.notenoughupdates.moulconfig"
    version = tag ?: hash
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.nea.moe/releases")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://maven.neoforged.net/releases")
    }
    afterEvaluate {
//        (tasks.findByName("dokkaHtml") as? DokkaTask)?.apply {
//            dokkaSourceSets {
//                "main" {
//                    sourceLink {
//                        println(project.path)
//                        localDirectory.set(file("src/main/"))
//                        remoteUrl.set(URL("https://github.com/NotEnoughUpdates/MoulConfig/blob/$hash/src/main/"))
//                        remoteLineSuffix.set("#L")
//                    }
//                }
//            }
//        }
        extensions.findByType<PublishingExtension>()?.apply {
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
            publications.filterIsInstance<MavenPublication>().forEach {
                it.pom {
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
}
subprojects {
    if (plugins.hasPlugin("org.jetbrains.dokka"))
        dependencies {
            "dokkaPlugin"("org.jetbrains.dokka:kotlin-as-java-plugin:1.9.10")
        }
}