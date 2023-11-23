import xyz.wagyourtail.unimined.api.task.RemapJarTask

plugins {
    java
    idea
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    `maven-publish`
    id("xyz.wagyourtail.unimined")
    kotlin("plugin.lombok")
}

unimined.minecraft {
    version("1.20.2")
    mappings {
        intermediary()
        yarn(1)
    }

    fabric {
        loader("0.14.22")
    }
//    runs {
//        config("client") {
//            this.jvmArgs.add("-Dmoulconfig.testmod=true")
//            this.jvmArgs.add("-Dmoulconfig.warn.crash=false")
//        }
//    }
}


java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

val shadowInclude by configurations.creating
dependencies {
    implementation(project(":common"))
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:0.89.2+1.20.2")
    shadowInclude(project(":common", configuration = "singleFile"))
}

tasks.shadowJar {
    configurations = listOf(shadowInclude)
    archiveClassifier.set("dev")
}

val remapJar by tasks.named("remapJar", RemapJarTask::class) {
    archiveClassifier.set("")
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

tasks.jar {
    archiveClassifier.set("small")
    dependsOn(tasks.processResources)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(remapJar) {
                classifier = ""
            }
            artifact(tasks.shadowJar) {
                classifier = "named"
            }
        }
    }
}



