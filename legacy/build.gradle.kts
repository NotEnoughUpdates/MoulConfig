import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.wagyourtail.unimined.api.task.RemapJarTask

plugins {
    idea
    java
    `maven-publish`
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("com.github.johnrengelman.shadow")
    id("xyz.wagyourtail.unimined")
    kotlin("plugin.lombok")
}

unimined.minecraft {
    version("1.8.9")
    mappings {
        searge()
        mcp("stable", "22-1.8.9")
    }
    minecraftForge {
        loader("11.15.1.2318-1.8.9")
    }
    runs {
        config("client") {
            this.jvmArgs.add("-Dmoulconfig.testmod=true")
            this.jvmArgs.add("-Dmoulconfig.warn.crash=false")
        }
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val include by configurations.creating {
    isVisible = true
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("org.jetbrains:annotations:24.0.1")
    implementation((project(":common")))
    include(project(":common", configuration = "singleFile"))
}

sourceSets.main {
    output.setResourcesDir(java.classesDirectory)
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(KotlinCompile::class) {
    this.compilerOptions {
        this.jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.shadowJar {
    configurations = listOf(include)
}

tasks.dokkaHtml {
    dokkaSourceSets {
        ("main") {
            moduleName.set("MoulConfig-Forge")
            sourceRoots.from(sourceSets.main.get().allSource)
            classpath.from(tasks.compileJava.get().classpath)

            includes.from(fileTree("docs") { include("*.md") })
        }
    }
}


tasks.jar {
    archiveClassifier.set("small")
}
tasks.shadowJar {
    archiveClassifier.set("dev")
}
val remapJar by tasks.named("remapJar", RemapJarTask::class) {
    archiveClassifier.set("")
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

val libraryJar by tasks.creating(Jar::class) {
    from(zipTree(remapJar.archiveFile))
    dependsOn(remapJar)
    archiveClassifier.set("notest")
    exclude("io/github/moulberry/moulconfig/test/*")
    exclude("mcmod.info")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(libraryJar) {
                classifier = ""
            }
            artifact(remapJar) {
                classifier = "test"
            }
            artifact(tasks.shadowJar) {
                classifier = "named"
            }
        }
    }
}


