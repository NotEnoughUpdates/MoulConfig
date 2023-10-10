plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.dokka")
    kotlin("plugin.lombok")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}
dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("org.jetbrains:annotations:24.0.1")
    implementation("com.google.code.gson:gson:2.1")
}
val singleFile by configurations.creating
artifacts {
    add(singleFile.name, tasks.jar)
}
tasks.dokkaHtml {
    dokkaSourceSets {
        ("main") {
            moduleName.set("MoulConfig-Common")
            sourceRoots.from(sourceSets.main.get().allSource)
            classpath.from(tasks.compileJava.get().classpath)
        }
    }
}
