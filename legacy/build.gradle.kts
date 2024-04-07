import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    idea
    java
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
//    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.shadow)
    alias(libs.plugins.unimined)
    alias(libs.plugins.dokka)
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
            this.jvmArgs.add("-Dmoulconfig.testmod=true")
            this.jvmArgs.add("-Dmoulconfig.warn.crash=false")
            this.jvmArgs.remove("-XX:+UseG1GC")
            this.jvmArgs.remove("-XX:G1NewSizePercent=20")
            this.jvmArgs.remove("-XX:MaxGCPauseMillis=50")
            this.jvmArgs.remove("-XX:G1ReservePercent=20")
            this.jvmArgs.remove("-XX:G1HeapRegionSize=32M")
            this.env.put(
                "LD_LIBRARY_PATH",
                ":/nix/store/agp6lqznayysqvqkx4k1ggr8n1rsyi8c-gcc-13.2.0-lib/lib:/nix/store/ldi0rb00gmbdg6915lhch3k3b3ib460z-libXcursor-1.2.2/lib:/nix/store/8xbbv82pabjcbj30vrna4gcz4g9q97z4-libXrandr-1.5.4/lib:/nix/store/smrb2g0addhgahkfjjl3k8rfd30gdc29-libXxf86vm-1.1.5/lib:/nix/store/lpqy1z1h8li6h3cp9ax6vifl71dks1ff-libglvnd-1.7.0/lib"
            )
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
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)
    compileOnly(libs.jbAnnotations)
    implementation((project(":common")))
    implementation(libs.libninepatch)
    include(libs.libninepatch)
    include(project(":common", configuration = "singleFile"))
}

sourceSets.main {
    output.setResourcesDir(java.classesDirectory)
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.processResources {
    from(project(":common").tasks.processResources)
}

tasks.withType(KotlinCompile::class) {
    this.compilerOptions {
        this.jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.shadowJar {
    configurations = listOf(include)
}

tasks.jar {
    archiveClassifier.set("small")
}
tasks.shadowJar {
    archiveClassifier.set("dev")
}
val sourcesJar by tasks.creating(Jar::class) {
    from(sourceSets.main.get().allSource)
    from(project(":common").sourceSets.getByName("main").allSource)
    archiveClassifier.set("sources")
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
            artifact(tasks["sourcesJar"]) {
                classifier = "sources"
            }
            artifact(tasks.shadowJar) {
                classifier = "named"
            }
        }
    }
}


