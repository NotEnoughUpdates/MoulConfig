import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    id("moulconfig.dokka")
	id("moulconfig.leaf")
	id("moulconfig.kotlin")
}

tasks.withType(JavaCompile::class) {
    this.javaCompiler.set(javaToolchains.compilerFor {
        this.languageVersion.set(JavaLanguageVersion.of(8))
    })
}

unimined.minecraft {
    version(libs.versions.mc.legacy.get())
    mappings {
        searge()
        mcp("stable", libs.versions.mcp.get())
    }
    minecraftForge {
        loader(libs.versions.forge.loader.get())
    }
    runs {
        config("client") {
			jvmArgs("-Dmoulconfig.testmod=true")
            jvmArgs("-Dmoulconfig.warn.crash=false")
        }
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.JB_ANNOTATIONS)
}

sourceSets.main {
    output.setResourcesDir(java.classesDirectory)
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.processResources {
    exclude("fabric.mod.json")
}

tasks.withType(KotlinCompile::class) {
    this.compilerOptions {
        this.jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.jar {
    archiveClassifier.set("small")
}

val remapJar by tasks.named("remapJar", RemapJarTask::class) {
	asJar {
		archiveClassifier.set("")
	}
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

val libraryJar by tasks.creating(Jar::class) {
    from(zipTree(remapJar.asJar.archiveFile))
    dependsOn(remapJar)
    archiveClassifier.set("notest")
    exclude("io/github/notenoughupdates/moulconfig/test/*")
    exclude("mcmod.info")
}

publishing {
    publications {
        defaultMaven {
            artifact(libraryJar) {
                classifier = ""
            }
            artifact(remapJar) {
                classifier = "test"
            }
        }
    }
}


