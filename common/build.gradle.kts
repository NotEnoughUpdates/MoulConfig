plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.dokka)
    `maven-publish`
	id("moulconfig.base")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

dependencies {
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.JB_ANNOTATIONS)
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



