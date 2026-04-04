plugins {
    java
    id("moulconfig.kotlin")
    id("moulconfig.dokka.base")
    `maven-publish`
    id("moulconfig.base")
    id("moulconfig.test")
    id("moulconfig.manifold")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

dependencies {
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.JB_ANNOTATIONS)
    compileOnly(Dependencies.JSPECIFY)
    implementation(Dependencies.LIB_NINE_PATCH)
    compileOnly(Dependencies.LEGACY_GSON)
}
val singleFile by configurations.creating
artifacts {
    add(singleFile.name, tasks.jar)
}

val sourcesJar by tasks.creating(Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        defaultMaven {
            artifact(tasks.jar) {
                classifier = ""
            }
            artifact(sourcesJar) {
                classifier = "sources"
            }
        }
    }
}

dokka {
    val modern2611 = project(":modern:modern-26.1")
    modern2611.afterEvaluate {
        dokkaSourceSets.configureEach {
            val modernSource =
                modern2611.sourceSets.main.map { it.allSource }
//            sourceRoots.from(modernSource)
        }
    }
}


