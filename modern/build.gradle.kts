import xyz.wagyourtail.unimined.api.task.RemapJarTask

plugins {
    java
    idea
    `maven-publish`
    alias(libs.plugins.unimined)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.dokka)
    alias(libs.plugins.shadow)

}

unimined.minecraft {
    version(libs.versions.mc.modern.get())
    mappings {
        intermediary()
        yarn(libs.versions.yarn.build.get())
    }

    fabric {
        loader(libs.versions.fabric.loader.get())
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
    "modImplementation"(libs.fabric.api)
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



