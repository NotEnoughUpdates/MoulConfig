plugins {
    id("moulconfig.dokka.base")
}

dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(project.file("src/"))
            remoteUrl("https://github.com/NotEnoughUpdates/MoulConfig/blob/${Version.hash}/${project.name}/src")
            remoteLineSuffix.set("#L")
        }
        sourceRoots.setFrom(project.file("src/main/kotlin"), project.file("src/main/java"))
    }
    modulePath.set(project.path.removePrefix(":").replace(":", "/"))
}

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:2.1.0")
}
