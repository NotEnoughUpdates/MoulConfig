plugins {
    java
    id("moulconfig.kotlin")
    id("moulconfig.dokka")
    `maven-publish`
    id("moulconfig.base")
    id("moulconfig.test")
    id("moulconfig.allopen")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

dependencies {
    annotationProcessor(Dependencies.LOMBOK)
    compileOnly(Dependencies.LOMBOK)
    compileOnly(Dependencies.JB_ANNOTATIONS)
    implementation(project(":common"))
}

kotlin{
    allOpen {
        annotation("moe.nea.shale.util.AllOpen")
    }
}

