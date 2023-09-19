plugins {
    java
    kotlin("jvm")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}
dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("org.jetbrains:annotations:24.0.1")
    implementation("com.google.code.gson:gson:2.1")
}