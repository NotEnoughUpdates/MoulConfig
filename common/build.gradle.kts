plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.lombok)
    alias(libs.plugins.dokka)
    `maven-publish`
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}
dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)
    compileOnly(libs.jbAnnotations)
    implementation(libs.legacyGson)
}
val singleFile by configurations.creating
artifacts {
    add(singleFile.name, tasks.jar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.jar) {
                classifier = ""
            }
        }
    }
}



